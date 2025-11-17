import Navbar from "../components/Navbar";

const Layout = ({ children }) => {
  return (
    <div className="flex flex-col h-screen">
      <Navbar />
      <main className="flex-1 flex flex-col bg-gray-100 dark:bg-gray-900 pt-16 overflow-auto">
        {children}
      </main>
    </div>
  );
};

export default Layout;

/*

import React from "react";
import Navbar from "../Components/Navbar";

const Layout = ({ children }) => {

  return (
    <div className="flex flex-col h-screen">
      <Navbar />
      <main className="flex-1 flex flex-col bg-gray-100 dark:bg-gray-900 pt-16 min-h-0">
        {children}
      </main>
    </div>
  );
};

export default React.memo(Layout);

is this sold
*/