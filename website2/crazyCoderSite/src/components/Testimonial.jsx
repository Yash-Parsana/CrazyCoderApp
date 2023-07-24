import { useEffect, useState } from "react";
import Header from "./Header";

const testimonials = [
  {
    id: 1,
    name: "John Smith",
    text: "CrazyCoder's collaborative community has accelerated my learning and inspired me to tackle ambitious coding projects. Thanks to the talented and supportive developers, I've been able to level up my skills and overcome coding challenges with ease.",
    image: "boy.png",
  },
  {
    id: 2,
    name: "Emily Johnson",
    text: "Through expert sessions at CrazyCoder, I've gained valuable knowledge from industry professionals, opening doors to exciting career opportunities and enhancing my personal and professional growth in the competitive programming field.",
    image: "girl.jpg",
  },
  {
    id: 3,
    name: "Daniel Williams",
    text: "CrazyCoder's code reviews have significantly improved the quality of my work. Constructive feedback from experienced developers has helped me refine my coding skills and develop a more efficient and effective programming style.",
    image: "boy.png",
  },
  {
    id: 4,
    name: "Sophia Anderson",
    text: "CrazyCoder's hackathons have fueled my problem-solving abilities and creativity. Collaborating with talented developers, I've had the opportunity to innovate and showcase my skills in competitive programming competitions.",
    image: "girl.jpg",
  },
  {
    id: 5,
    name: "Oliver Thompson",
    text: "The mentorship program at CrazyCoder has been a game-changer for my development journey. With a seasoned developer guiding me, I've set goals, received personalized support, and accelerated my growth in the competitive programming community.",
    image: "boy.png",
  },
  {
    id: 6,
    name: "Emma Davis",
    text: "CrazyCoder's inclusive and supportive community has been invaluable as a beginner programmer. The encouragement and guidance I've received from fellow members have boosted my confidence and helped me overcome coding obstacles.",
    image: "girl.jpg",
  },
  {
    id: 7,
    name: "Michael Brown",
    text: "As an experienced developer, CrazyCoder has provided me with a platform to share my knowledge and contribute to the growth of others. Mentoring aspiring programmers and giving back to the community has been fulfilling and rewarding.",
    image: "boy.png",
  },
];

const Testimonial = ({ testimonial }) => (
  <div className="bg-gray-100 rounded-[50px] w-full delay-150 duration-300 p-10 hover:scale-110 md:m-6 m-2 mt-8 h-100 border-2 border-white">
    <p className="text-black">{testimonial.text}</p>
    <div className="flex items-center mt-4">
      <div className="">
        <img
          className="w-12 h-12 rounded-full"
          src={testimonial.image}
          alt={testimonial.name}
        />
      </div>
      <div className="ml-4">
        <div className="text-black font-medium">{testimonial.name}</div>
      </div>
    </div>
  </div>
);

const Testimonials = () => {
  const [current, setCurrent] = useState(0);
  const [numVisible, setNumVisible] = useState(3);

  useEffect(() => {
    const handleResize = () => {
      const screenWidth = window.innerWidth;
      if (screenWidth < 640) {
        setNumVisible(1);
      } else if (screenWidth < 768) {
        setNumVisible(2);
      } else {
        setNumVisible(3);
      }
    };

    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  const visibleTestimonials = testimonials.slice(current, current + numVisible);

  const goToPrev = () => {
    if (current === 0) {
      setCurrent(testimonials.length - numVisible);
    } else {
      setCurrent(current - 1);
    }
  };

  const goToNext = () => {
    if (current === testimonials.length - numVisible) {
      setCurrent(0);
    } else {
      setCurrent(current + 1);
    }
  };

  return (
    <div className="py-10 px-5">
      <Header name="Testimonials" />
      <div className="flex flex-col items-center justify-center">
        <div className="flex flex-row items-center justify-center mb-6 gap-6">
          <button
            onClick={goToPrev}
            className="bg-gray-100 text-purple-600 rounded-full p-4 flex justify-center items-center ml-2"
          >
            {"<"}
          </button>
          {visibleTestimonials.map((testimonial) => (
            <div key={testimonial.id}>
              <Testimonial testimonial={testimonial} />
            </div>
          ))}
          <button
            onClick={goToNext}
            className="bg-gray-100 text-purple-600 rounded-full p-4 flex justify-center items-center ml-10"
          >
            {">"}
          </button>
          <div className="w-2 " />
        </div>
        <div className="flex justify-center">
          {testimonials.map((testimonial, index) => (
            <div
              key={testimonial.id}
              className={`h-2 w-2 rounded-full mx-2 bg-purple-600 ${
                current === index ? "bg-gray-400" : "bg-gray-900"
              }`}
              onClick={() => setCurrent(index)}
            ></div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Testimonials;
