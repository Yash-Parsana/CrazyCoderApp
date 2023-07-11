import React from "react";
import Header from "./Header";

const platforms = [
  "CodeChef",
  "LeetCode",
  "AtCoder",
  "TopCoder",
  "HackerRank",
  "Kaggle",
  "Coderbyte",
  "Project Euler",
];

const PlatformCaters = () => {
  return (
    <div className="py-10 px-5">
      <Header name="Platforms Available" />
      <div className="flex flex-wrap justify-center max-w-2xl mx-auto">
        {platforms.map((platform) => (
          <div
            key={platform}
            className="bg-purple-600 p-4 rounded-[50px] text-white font-bold m-2 transform hover:scale-105 transition-all"
          >
            {platform}
          </div>
        ))}
      </div>
    </div>
  );
};

export default PlatformCaters;

