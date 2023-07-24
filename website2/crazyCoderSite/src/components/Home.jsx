import React, {useState, useEffect} from 'react'
import { FaGooglePlay } from 'react-icons/fa';

const Home = () => {
    const [isLargeScreen, setIsLargeScreen] = useState(window.innerWidth > 1024);

  useEffect(() => {
    const handleResize = () => {
      setIsLargeScreen(window.innerWidth > 1024);
    };

    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  return (
    <>
      <div className={`flex${isLargeScreen ? "" : "-column"} h-screen`}>
        <div className="w-full sm:w-3/5 bg-white flex flex-col justify-center items-start p-8 ml-8">
          <h1 className="text-5xl text-purple-600 font-bold mb-4">
            All your competitive <br /> coding needs
          </h1>
          <p className="text-2xl font-bold mb-8">
            The ultimate platform for <br /> competitive programmers
          </p>
          <a
            href="https://play.google.com/store/apps/details?id=com.parsanatech.crazycoder"
            className="bg-black hover:bg-gray-900 text-white font-bold py-4 px-6 rounded-lg flex items-center z-40"
            target="_blank"
            rel="noopener noreferrer"
          >
            <FaGooglePlay className="mr-2" />
            Download on Google Play
          </a>
        </div>
        {isLargeScreen && (
          <>
            <div className="w-2/5 bg-purple-600 relative">
              <div className="absolute inset-20 pt-10 flex items-center justify-center z-10">
                <img
                  src="/Phone-Image2.png"
                  alt="iPhone Mockup"
                  className="max-w-full max-h-full"
                />
              </div>
            </div>
            <div className="absolute inset-20 pt-10 pl-40 flex items-center justify-center z-10">
              <img
                src="/Phone-Image3.png"
                alt="iPhone Mockup"
                className="max-w-full max-h-full"
              />
            </div>
          </>
        )}
        {!isLargeScreen && (
          <div className="flex justify-center w-full bg-purple-500">
            <div className="absolute inset flex items-center justify-center z-10">
              <img
                src="/Phone-Image2.png"
                alt="iPhone Mockup"
                className="max-w-full h-screen"
              />
            </div>
          </div>
        )}
      </div>
      <div className="h-36"></div>
  </>
  )
}

export default Home