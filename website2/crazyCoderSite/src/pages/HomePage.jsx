import React from "react";

import FeatureSection from "../components/FeatureSection";
import FeatureSection2 from "../components/FeatureSection2";
import Testimonials from "../components/Testimonial";
import PlatformCaters from "../components/PlatformCaters";
import Footer from "../components/Footer";
import Home from "../components/Home";

const HomePage = () => {
  return (
    <>
      <Home />
      <FeatureSection />
      <FeatureSection2 />
      <Testimonials />
      <PlatformCaters />
      <Footer />
    </>
  );
};

export default HomePage;
