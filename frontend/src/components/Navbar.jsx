import React, { useEffect, useRef, useState } from "react";
import ThemeToggleMobile from "./ThemeToggleMobile";
import { FaUserCircle, FaCog, FaAdjust, FaSignOutAlt } from "react-icons/fa";
import PricingModal from "./PricingModal";
import { useAuth } from "../context/AuthContext";
import { Roles } from "../utils/roles";
import { Link } from "react-router-dom";
import ThemeToggle from "./ThemeToggle";

const Navbar = () => {

  const { user, login, logout, isAuthenticated } = useAuth();

  const [isMobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [isPricingModalOpen, setPricingModalOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const menuRef = useRef();

  useEffect(() => {

    const handleClickOutside = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setUserMenuOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);

  }, [])

  return (
    <nav className="fixed top-0 left-0 w-full z-50 bg-gray-200 dark:bg-[#0d1421] px-4 py-4 flex items-center justify-between shadow transition-colors duration-300">
      <Link to="/" className="flex items-center space-x-2">
        <img src="/blockalert-icon.png" alt="BlocAlert" className="h-6 w-6 md:h-8 md:w-8" />
        <span className="font-bold text-lg text-gray-900 dark:text-white">
          BlocAlert
        </span>
      </Link>

      <div className="flex items-center space-x-6">

        {(isAuthenticated && user?.role === Roles.FREE_USER) && (
          <button
            onClick={() => setPricingModalOpen(!isPricingModalOpen)}
            className="bg-blue-500 text-white px-4 py-1.5 rounded-xl shadow-md hover:shadow-lg hover:brightness-110 transition-all duration-200"
          >
            Upgrade
          </button>
        )}

        {!isAuthenticated ? (
          <>
            <div className="flex space-x-3">
              <div className="hidden md:flex items-center space-x-2 gap-3">
                <button className="bg-blue-600 px-4 py-1 rounded-lg hover:bg-blue-500 text-white text-sm md:text-lg transition-colors duration-200"
                  onClick={() => login({
                    authorizationParams: {
                      screen_hint: "signup",
                    }
                  })}
                >
                  Sign Up
                </button>
                <button className="bg-blue-600 px-4 py-1 rounded-lg hover:bg-blue-500 text-white text-sm md:text-lg transition-colors duration-200"
                  onClick={() => login()}>
                  Log In
                </button>
              </div>
            </div>
            < button
              className="md:hidden text-gray-900 dark:text-gray-400 hover:text-gray-600 dark:hover:text-white text-xl transition-colors duration-200"
              onClick={() => setMobileMenuOpen(!isMobileMenuOpen)}
            >
              ☰
            </button>
          </>
        ) : (

          <div className="relative" ref={menuRef}>
            <button
              onClick={() => setUserMenuOpen((prev) => !prev)}
              className="flex items-center space-x-2 focus:outline-none hover:bg-gray-100 dark:hover:bg-gray-800 px-3 py-2 rounded-xl transition-all duration-200"
            >
              <FaUserCircle className="text-3xl text-gray-600 dark:text-gray-300" />
            </button>


            {userMenuOpen && (

              <div className="absolute right-0 mt-3 w-60 rounded-2xl bg-white/95 dark:bg-gray-900/95 backdrop-blur-md border border-gray-200/30 dark:border-gray-700/40 shadow-2xl py-2 z-50 animate-fadeIn origin-top-right">
                <div className="px-4 py-2 border-b border-gray-100 dark:border-gray-800">
                  <p className="text-sm text-gray-500 dark:text-gray-400">Welcome</p>
                  <p className="font-semibold text-gray-900 dark:text-white truncate">
                    {user?.name || user?.email || "User"}
                  </p>
                </div>

                <Link className="flex items-center w-full text-left px-4 py-2 text-gray-700 dark:text-gray-200 hover:bg-gray-100/80 dark:hover:bg-gray-800/70 rounded-lg transition-all duration-200"
                  to="/settings"
                >
                  <FaCog className="mr-3" />
                  Settings
                </Link>

                {/* Theme Toggle with Icon */}
                <div
                  className="flex items-center justify-between w-full px-4 py-2 text-gray-700 dark:text-gray-200 hover:bg-gray-100/80 dark:hover:bg-gray-800/70 rounded-lg transition-all duration-200 cursor-pointer"
                >
                  <div className="flex items-center gap-2">
                    <FaAdjust className="text-indigo-500 dark:text-indigo-400" />
                    <span>Theme</span>
                  </div>
                  <ThemeToggle />
                </div>

                {/* Logout */}
                <button
                  onClick={() =>
                    logout({ logoutParams: { returnTo: window.location.origin } })
                  }
                  className="flex items-center w-full text-left px-4 py-2 text-red-600 hover:bg-red-50 dark:hover:bg-gray-800/70 rounded-lg transition-all duration-200"
                >
                  <FaSignOutAlt className="mr-2" /> Log Out
                </button>

              </div>
            )}
          </div>
        )}
      </div>

      {/* Mobile menu */}
      {!isAuthenticated && isMobileMenuOpen && (

        <div className="absolute top-full left-0 w-full max-h-[70vh] overflow-auto bg-gray-200 dark:bg-[#0d1421] flex flex-col md:hidden px-4 py-4 space-y-3 z-50 border-t border-gray-400 dark:border-gray-700">
          <button
            className="w-full bg-blue-600 hover:bg-blue-500 text-white py-2 rounded text-center font-semibold transition-colors duration-200"
            onClick={() => login({
              authorizationParams: {
                screen_hint: "signup",
              }
            })}
          >
            Sign Up
          </button>
          <button
            className="w-full bg-blue-600 hover:bg-blue-500 text-white py-2 rounded text-center font-semibold transition-colors duration-200"
            onClick={() => login()}
          >
            Log In
          </button>
        </div>
      )
      }
      {
        isPricingModalOpen &&
        <PricingModal onClose={() => setPricingModalOpen(!isPricingModalOpen)} />
      }
    </nav >
  );
};

export default React.memo(Navbar);
