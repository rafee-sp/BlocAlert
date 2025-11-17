export const toggleTheme = (theme) => {
  document.documentElement.classList.toggle("dark", theme === "dark");
};
