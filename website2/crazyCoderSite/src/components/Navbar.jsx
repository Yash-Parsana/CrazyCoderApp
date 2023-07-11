import React from "react";
import { Link } from "react-router-dom";

const Navbar = () => {
  return (
    <header className="bg-white">
      <div className="mx-auto flex h-20 max-w-screen-xl items-center gap-4 px-4 sm:px-6 lg:px-8">
        <Link to="/" className="block text-purple-600 text-2xl font-bold">
          CRAZY CODER
        </Link>

        <div className="flex flex-1 items-center justify-end md:justify-between">
          <nav aria-label="Global" className="hidden md:block">
            <ul className="flex items-center gap-6 text-2xl">
              {/* Add menu items as needed */}
            </ul>
          </nav>

          <div className="flex flex-row items-center gap-4">
            <div className="sm:flex flex sm:gap-4">
              <Link
                to="/login"
                className="block rounded-md bg-purple-600 px-6 py-3 text-md font-medium text-white transition hover:bg-purple-700"
              >
                Login
              </Link>

              <Link
                to="/signup"
                className="block rounded-md bg-gray-100 px-5 py-2.5 text-md font-medium text-purple-600 transition hover:text-purple-600/75 sm:block"
              >
                Register
              </Link>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
