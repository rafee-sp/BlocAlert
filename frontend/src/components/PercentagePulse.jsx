import {useEffect, useRef, useState } from "react";
import { BiSolidUpArrow, BiSolidDownArrow } from "react-icons/bi";

const PercentagePulse = ({ value, children }) => {

    const [isPulsing, setPulsing] = useState(false);
    const prevValueRef = useRef(value);

    const baseColor = value >= 0 ? "text-green-500 dark:text-green-400" : "text-red-600 dark:text-red-500";

    const pulseColor = value >= 0 ? "text-green-300 dark:text-green-500" : "text-red-300 dark:text-red-600";

    useEffect(() => {

        if (value !== prevValueRef.current && prevValueRef !== undefined) {

            setPulsing(true);
            prevValueRef.current = value;

            const timeout = setTimeout(() => {
                setPulsing(false);
            }, 3000)

            return () => clearTimeout(timeout);

        } else if (prevValueRef.current === undefined) {
            prevValueRef.current = value;
        }

    }, [value])

    return (

        <span className={`inline-flex font-semibold items-center gap-2 transition-colors duration-3000 ${isPulsing ? pulseColor : baseColor}`}>
            {value >=0 ? (<BiSolidUpArrow className="text-xs"/>) : (<BiSolidDownArrow className="text-xs"/>)}{children}
        </span>

    )

}

export default PercentagePulse;