import { useEffect, useState } from "react";

const DataPulse = ({value, children}) => {
  const [isPulsing, setPulsing] = useState(false);
  
  useEffect(() => {
 
      setPulsing(true);
            
      const timeout = setTimeout(() => setPulsing(false), 1000);
      return () => clearTimeout(timeout);
    
  },[value]);

  
  return (
    <span className={`transition-all duration-1000 ${isPulsing ? "text-blue-500 dark:text-blue-500" : "text-gray-800 dark:text-white"} `}>{children} </span>
  );
};

export default DataPulse;