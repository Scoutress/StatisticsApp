package com.scoutress.KaimuxAdminStats.servicesimpl.minecraftTickets;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.scoutress.KaimuxAdminStats.entity.employees.EmployeeCodes;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTickets;
import com.scoutress.KaimuxAdminStats.entity.minecraftTickets.MinecraftTicketsAnswers;
import com.scoutress.KaimuxAdminStats.repositories.minecraftTickets.MinecraftTicketsRepository;
import com.scoutress.KaimuxAdminStats.services.DataExtractingService;
import com.scoutress.KaimuxAdminStats.services.minecraftTickets.MinecraftTicketsService;

@Service
public class MinecraftTicketsServiceImpl implements MinecraftTicketsService {

	public final DataExtractingService dataExtractingService;
	public final MinecraftTicketsRepository minecraftTicketsRepository;

	public MinecraftTicketsServiceImpl(
			DataExtractingService dataExtractingService,
			MinecraftTicketsRepository discordTicketsRepository) {
		this.dataExtractingService = dataExtractingService;
		this.minecraftTicketsRepository = discordTicketsRepository;
	}

	@Override
	public void convertMinecraftTicketsAnswers() {

		List<EmployeeCodes> employeeCodes = extractEmployeeCodes();
		List<MinecraftTicketsAnswers> ticketsAnswers = extractDataFromAnswersTable();
		List<MinecraftTicketsAnswers> reactionsWithEmployeeIds = mapMinecraftToEmployeeIds(ticketsAnswers, employeeCodes);
		List<MinecraftTickets> convertedData = convertData(reactionsWithEmployeeIds);

		saveDataToNewTable(convertedData);
	}

	public List<EmployeeCodes> extractEmployeeCodes() {
		List<EmployeeCodes> data = dataExtractingService.getAllEmployeeCodes();
		return data;
	}

	public List<MinecraftTicketsAnswers> extractDataFromAnswersTable() {
		List<MinecraftTicketsAnswers> data = dataExtractingService.getAllMcTicketsAnswers();
		return data;
	}

	public List<MinecraftTickets> convertData(List<MinecraftTicketsAnswers> rawData) {
		Map<Long, Map<LocalDate, Long>> groupedData = rawData
				.stream()
				.collect(Collectors.groupingBy(
						MinecraftTicketsAnswers::getMinecraftTicketId,
						Collectors.groupingBy(
								reaction -> reaction.getDateTime().toLocalDate(),
								Collectors.counting())));

		return groupedData.entrySet()
				.stream()
				.flatMap(adminEntry -> adminEntry.getValue()
						.entrySet()
						.stream()
						.map(dateEntry -> new MinecraftTickets(
								null,
								adminEntry.getKey(),
								String.valueOf(dateEntry.getValue()),
								dateEntry.getKey())))
				.collect(Collectors.toList());
	}

	public List<MinecraftTicketsAnswers> mapMinecraftToEmployeeIds(
			List<MinecraftTicketsAnswers> answers,
			List<EmployeeCodes> employeeCodes) {

		Map<Short, Short> minecraftToEmployeeMap = employeeCodes
				.stream()
				.filter(code -> code.getMinecraftId() != null)
				.filter(code -> code.getEmployeeId() != null)
				.collect(Collectors
						.toMap(
								EmployeeCodes::getMinecraftId,
								EmployeeCodes::getEmployeeId));

		return answers
				.stream()
				.map(reaction -> {
					Short employeeId = minecraftToEmployeeMap
							.get(reaction
									.getMinecraftTicketId()
									.shortValue());
					if (employeeId != null) {
						reaction
								.setMinecraftTicketId(employeeId
										.longValue());
					}
					return reaction;
				})
				.collect(Collectors.toList());
	}

	private void saveDataToNewTable(List<MinecraftTickets> convertedData) {
		convertedData.sort((a, b) -> a.getDate().compareTo(b.getDate()));
		convertedData.forEach(minecraftTicketsRepository::save);
	}
}