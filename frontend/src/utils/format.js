import { format, formatDistanceToNow } from "date-fns";

export const formatPrice = (price) => {
    
    if (price === null || price === undefined) return "-";
    
    if (price === 0) return "$0";

    let decimals ;

    if(price >= 1) {
        decimals = 2;
            
    } else {
        decimals = Math.min(8, Math.abs(Math.floor(Math.log10(price))) + 3);        
    }

    const factor = Math.pow(10, decimals);
    const truncatedPrice = Math.floor(price * factor) / factor;
    
    return `$${truncatedPrice.toLocaleString("en-US", {minimumFractionDigits : decimals, maximumFractionDigits : decimals})}`;
}

export const formatPercentage = (value) => {
    if(value === null || value === undefined) return "-";
    return Math.abs(value).toFixed(2) + "%";
}

export const formatNumberCompact = (value) => {
    if(!value) return "-";
    if (value >= 1_000_000_000_000) return (value / 1_000_000_000_000).toFixed(2) + "T";
    if(value >= 1_000_000_000) return (value / 1_000_000_000).toFixed(2) +"B ";
    if(value >= 1_000_000) return (value / 1_000_000).toFixed(2) +"M ";
    if(value >= 1_000) return (value / 1_000).toFixed(2) +"K ";
    return "-";
}

export const formatSupply = (value, symbol) => {
    if(!value) return "-";
    if (value >= 1_000_000_000_000) return (value / 1_000_000_000_000).toFixed(2) + "T "+ symbol.toUpperCase();
    if(value >= 1_000_000_000) return (value / 1_000_000_000).toFixed(2) +"B " + symbol.toUpperCase();
    if(value >= 1_000_000) return (value / 1_000_000).toFixed(2) +"M " + symbol.toUpperCase();
    if(value >= 1_000) return (value / 1_000).toFixed(2) +"K " + symbol.toUpperCase();
    return "-";
}

export const formatDateWithText = (isoDate) => {

    if(!isoDate) return "-";
    const date = new Date(isoDate);
    return `${format(date, "MMMM d, yyyy")} (${formatDistanceToNow(date, {addSuffix : true})})`;
}

export const formatDate = (dateStr) => {

    if(!dateStr) return "-";
    const date = new Date(dateStr); 
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" }); // Jan 01, 2025

}

export const formatDateTime = (dateStr) => {

    if(!dateStr) return "-";
    const date = new Date(dateStr); 
    return date.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric", hour:"2-digit", minute:"2-digit", hour12:true}); // Jan 01, 2025 2:30 PM

}