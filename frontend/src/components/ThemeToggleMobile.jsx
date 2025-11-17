import { useState } from "react";

const ThemeToggle = ({ onChange, defaultTheme }) => {

  /*
  const [theme, setTheme] = useState(defaultTheme ?? "dark"); // default theme

  console.log("child theme called");
  const handleChange = (newTheme) => {
    setTheme(newTheme);
    onChange();
    // Optional: apply theme to your app here
  };

  const options = ["light", "dark"];

  return (
    <div className="relative w-60 bg-gray-800 rounded-full p-1 flex justify-between items-center cursor-pointer select-none">
      
<div
  className={`absolute top-0 left-0 h-full w-1/3 bg-blue-600 rounded-full transition-all duration-300`}
  style={{
    transform:
      theme === "light" ? "translateX(0%)" : "translateX(200%)"

  }}
/>

{
  options.map((opt, idx) => (
    <div
      key={opt}
      className="z-10 w-1/3 text-center text-white text-sm font-medium py-1"
      onClick={() => handleChange(opt)}
    >
      {opt.charAt(0).toUpperCase() + opt.slice(1)}
    </div>
  ))
}
    </div >
  );
  */
};

export default ThemeToggle;
