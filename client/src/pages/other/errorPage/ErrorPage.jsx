import styles from "./ErrorPage.module.scss";

const ErrorPage = () => {
  return (
    <div className={styles.container}>
      <h1 className={styles.title}>404</h1>
      <p className={styles.message}>
        Oi! Puslapis, kurio ieškote, neegzistuoja.
      </p>
      <p className={styles.description}>
        Atrodo, kad nuoroda, kurią paspaudėte, gali būti sugadinta arba puslapis
        buvo pašalintas. Patikrinkite URL arba grįžkite į pagrindinį puslapį.
      </p>
      <a href="/" className={styles.homeLink}>
        Grįžti į pagrindinį puslapį
      </a>
    </div>
  );
};

export default ErrorPage;
