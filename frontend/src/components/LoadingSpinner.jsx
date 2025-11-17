import React from "react";

const LoadingSpinner = ({ compact }) => {
  
  const spinnerSize = compact ? "w-12 h-12 md:w-16 md:h-16" : "w-18 h-18 md:w-32 md:h-32";
  const logoSize = compact ? "w-10 h-10 md:w-14 md:h-14" : "w-14 h-14 md:w-28 md:h-28";

  return (
    <div
      className={`flex flex-col items-center justify-center 
                  ${compact ? "h-full" : "min-h-screen"} 
                  bg-gray-50 dark:bg-gray-800`}
    >
      <div className={`relative ${spinnerSize} flex items-center justify-center`}>
        {/* Spinner background ring */}
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="w-full h-full border-4 border-gray-200 dark:border-gray-700 rounded-full"></div>
          <div className="absolute w-full h-full border-4 border-transparent border-t-orange-500 rounded-full animate-spin"></div>
        </div>

        {/* Logo in the middle */}
        <div className={`relative ${logoSize} rounded-full overflow-hidden flex items-center justify-center`}>
          <img
            src="/logo.png"
            alt="BlockAlert Logo"
            className="w-full h-full object-cover"
          />
        </div>
      </div>

      {/* Loading text */}
      <div className="mt-4 text-center">
        <p className="text-gray-500 dark:text-gray-300 text-sm animate-pulse">
          Loading...
        </p>
      </div>
    </div>
  );
};

export default LoadingSpinner;
