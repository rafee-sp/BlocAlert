import React from "react";
import * as Sentry from "@sentry/react";
import { FiAlertTriangle } from "react-icons/fi";

class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    console.error("ErrorBoundary caught:", error, errorInfo);
    Sentry.captureException(error, { extra: errorInfo });
  }

  handleReload = () => window.location.reload();

  render() {
    if (!this.state.hasError) return this.props.children;

    return (
      <div className="flex flex-col items-center justify-center min-h-screen px-6 text-center 
                      bg-white text-gray-800 
                      dark:bg-gray-950 dark:text-gray-100 transition-colors duration-300">

        <div className="mb-6 animate-bounce">
          <FiAlertTriangle className="w-16 h-16 text-red-500 dark:text-red-400" />
        </div>

        <h1 className="text-3xl md:text-4xl font-bold mb-3">
          Oops! Something went wrong.
        </h1>

        <p className="text-gray-600 dark:text-gray-400 mb-8 max-w-md">
          {this.state.error?.message || 
            "An unexpected error occurred. Please try again later or contact support."}
        </p>

        {/* Buttons */}
        <div className="flex flex-col sm:flex-row gap-4">
          <button
            onClick={this.handleReload}
            className="px-6 py-2.5 rounded-lg font-medium bg-blue-600 hover:bg-blue-700 
                       text-white transition-colors duration-200"
          >
            Reload Page
          </button>

          <button
            onClick={() => window.history.back()}
            className="px-6 py-2.5 rounded-lg font-medium bg-gray-200 hover:bg-gray-300 
                       text-gray-800 dark:bg-gray-800 dark:text-gray-100 
                       dark:hover:bg-gray-700 transition-colors duration-200"
          >
            Go Back
          </button>
        </div>

        {/* Footer hint */}
        <p className="mt-10 text-sm text-gray-500 dark:text-gray-500">
          If the problem persists, our team has been notified.
        </p>
      </div>
    );
  }
}

export default ErrorBoundary;
