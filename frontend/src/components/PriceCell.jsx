import { useEffect, useState } from "react";

export default function PriceCell({ price }) {
  const [prevPrice, setPrevPrice] = useState(price);
  const [flash, setFlash] = useState("");

  useEffect(() => {
    if (price > prevPrice) {
      setFlash("up");
    } else if (price < prevPrice) {
      setFlash("down");
    }
    setPrevPrice(price);

    const timer = setTimeout(() => setFlash(""), 500); // remove flash after 0.5s
    return () => clearTimeout(timer);
  }, [price]);

  return (
    <td
      className={`py-3 px-4 font-semibold transition-colors duration-300 ${
        flash === "up"
          ? "bg-green-100 text-green-700"
          : flash === "down"
          ? "bg-red-100 text-red-700"
          : "text-white"
      }`}
    >
      {price.toLocaleString("en-US", {
        style: "currency",
        currency: "USD",
      })}
    </td>
  );
}
