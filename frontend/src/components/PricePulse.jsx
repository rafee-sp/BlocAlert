import { useEffect, useState, useRef } from "react";

const PricePulse = ({ value, children }) => {
  const [pulseColor, setPulseColor] = useState("");
  const prevValueRef = useRef(value);

  useEffect(() => {
    if (value !== prevValueRef.current && prevValueRef.current !== undefined) {
      
      if (value > prevValueRef.current) {
        setPulseColor("text-green-500");
      } else if (value < prevValueRef.current) {
        setPulseColor("text-red-500"); 
      }

      prevValueRef.current = value;
    
      const timeout = setTimeout(() => {
        setPulseColor("");
      }, 3000);

      return () => clearTimeout(timeout);
    } else if (prevValueRef.current === undefined) {

      prevValueRef.current = value;
    }
  }, [value]);

  return (
    <span
      className={`transition-colors duration-3000 ${pulseColor || "text-gray-900 dark:text-white"}`}
    >
      {children}
    </span>
  );
};

export default PricePulse;