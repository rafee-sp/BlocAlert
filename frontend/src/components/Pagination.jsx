import { useMemo } from "react";
import useMediaQuery from "../hooks/useMediaQuery";

export default function Pagination({ page, totalPages, onPageChange }) {

  const isMobileLayout = useMediaQuery("(max-width: 768px)");
  const surrondingPages = isMobileLayout ? 1 : 2;

  const pages = useMemo(() => {

    const range = [];

    range.push(1);

    let left = page - surrondingPages;
    let right = page + surrondingPages;

    if (left > 2) range.push("...");

    for (let i = Math.max(2, left); i <= Math.min(totalPages - 1, right); i++) {
      range.push(i);
    }

    if (right < totalPages - 1) range.push("...");

    if (totalPages > 1) range.push(totalPages);

    return range;
  }, [page, totalPages, surrondingPages]);

  if (totalPages <= 1) return null;

  return (
    <div className="flex flex-wrap justify-center gap-4 mt-4 text-lg md:text-xl">
      {pages.map((p, idx) =>
        p === "..." ? (
          <span
            key={idx}
            className="text-gray-500 dark:text-gray-400 select-none"
          >
            ...
          </span>
        ) : (
          <button
            key={idx}
            onClick={() => onPageChange(p)}
            className={`px-3 py-1 rounded transition-colors duration-200 border
          ${page === p
                ? "font-bold text-white bg-blue-500 border-blue-500"
                : "text-black dark:text-white border-transparent hover:border-gray-400 hover:bg-gray-200 dark:hover:bg-gray-700"
              }`}
          >
            {p}
          </button>
        )
      )}
    </div>

  );
}
