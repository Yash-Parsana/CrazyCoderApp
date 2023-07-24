import React from "react";
import { FaGooglePlay } from "react-icons/fa";

const FeatureSection2 = () => {
  return (
    <div className="flex bg-gray-100 py-10 h-[800px]">
      <div className="w-1/2 flex items-center justify-center">
        <div>
          <h1 className="text-5xl text-purple-600 font-bold mb-4">
            Chat with your friends
            <br /> about the contest
          </h1>
          <p className="text-2xl font-bold mb-8">
            The ultimate platform for <br /> competitive programmers
          </p>
          <a
            href="https://play.google.com/store/apps/details?id=com.parsanatech.crazycoder"
            className="bg-black hover:bg-gray-900 text-white font-bold py-4 px-6 rounded-lg flex items-center"
            target="_blank"
            rel="noopener noreferrer"
          >
            <FaGooglePlay className="mr-2" />
            Download on Google Play
          </a>
        </div>
      </div>
      <div className="w-1/2 flex justify-center items-center">
        <div className="relative">
          <img
            src="/Phone-Image1.png"
            alt="Image"
            className="absolute top-[100px] right-3 z-20 max-w-full max-h-full"
          />
          <div className="bg-purple-600 rounded-[500px] p-12 z-10 h-[500px] w-[500px] "></div>
        </div>
      </div>
    </div>
  );
};

export default FeatureSection2;
