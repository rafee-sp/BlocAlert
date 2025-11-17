import React, { useCallback, useEffect, useState } from "react";
import {
  FaBell,
  FaEdit,
  FaTrash,
  FaEnvelope,
  FaSms,
  FaChevronDown,
  FaChevronUp,
} from "react-icons/fa";
import AlertModal from "./AlertModal";
import { PRICE_CONDITIONS } from "../utils/alertConditions";
import { formatPrice, formatDateTime } from "../utils/format";
import { toast } from "react-toastify";
import SuccessToast from "./SuccessToast";
import InfiniteScroll from "react-infinite-scroll-component";
import LoadingSpinner from "./LoadingSpinner";
import authApi from "../api/authApi";
import ErrorToast from "./ErrorToast";

const AlertSection = ({ isExpanded, setIsExpanded, cryptoData }) => {

  const [activeTab, setActiveTab] = useState("active");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [activeAlerts, setActiveAlerts] = useState([]);
  const [pastAlerts, setPastAlerts] = useState([]);
  const [page, setPage] = useState({ pageNumber: 0 });
  const [pastAlertPage, setPastAlertPage] = useState({});
  const [hasMoreActive, setHasMoreActive] = useState(true);
  const [hasMorePast, setHasMorePast] = useState(true);
  const [loading, setLoading] = useState(false);
  const [selectedAlert, setSelectedAlert] = useState(null);
  const [currentPrice, setCurrentPrice] = useState(null);
  const [showOnlyCrypto, setShowOnlyCrypto] = useState(false);

  const DEFAULT_PAGE = 0;

  const fetchActiveAlerts = useCallback(async (pageNum = 0) => {

    if (loading) return;
    setLoading(true);

    try {

      const params = { page: pageNum, size: 10 };

      if (showOnlyCrypto && cryptoData?.id) params.cryptoId = cryptoData.id;

      const {
        data: { data: alertsArr, page: pageInfo },
      } = await authApi.get("/alerts/active", { params });

      if (pageNum === 0) {
        setActiveAlerts(alertsArr);
      } else {
        setActiveAlerts((prev) => [...prev, ...alertsArr]);
      }

      if (pageInfo) {
        setPage(pageInfo);
        setHasMoreActive(pageInfo.hasNext);
      }

    } catch (err) {
      console.error("Error fetching active alerts:", err);
      toast(<ErrorToast />)
      setHasMoreActive(false);
    } finally {
      setLoading(false);
    }
  }, [loading, showOnlyCrypto]);

  const fetchPastAlerts = useCallback(async (pageNum = 0) => {

    if (loading) return;
    setLoading(true);

    try {

      const params = { page: pageNum, size: 10 };

      if (showOnlyCrypto && cryptoData?.id) params.cryptoId = cryptoData.id;

      const {
        data: { data: alertsArr, page: pageInfo },
      } = await authApi.get("/alerts/history", { params });

      if (pageNum === 0) {
        setPastAlerts(alertsArr);
      } else {
        setPastAlerts((prev) => [...prev, ...alertsArr]);
      }

      if (pageInfo) {
        setPastAlertPage(pageInfo);
        setHasMorePast(pageInfo.hasNext);
      }
    } catch (err) {
      console.error("Error fetching active alerts:", err);
      toast(<ErrorToast />)
      setHasMorePast(false);
    } finally {
      setLoading(false);
    }
  }, [loading, showOnlyCrypto]);

  const handleDeleteAlert = useCallback(async (id) => {

    try {
      await authApi.delete(`/alerts/${id}`);
      setActiveAlerts((prev) => prev.filter((alert) => alert.id !== id));
      toast(<SuccessToast message={"Alert deleted successfully"} />);
    } catch (err) {
      console.error("Error deleting alert:", err);
      toast(<ErrorToast />)
    }
  }, []);

  // Initial fetch
  useEffect(() => {    
    if (cryptoData?.current_price) setCurrentPrice(cryptoData.current_price);
     fetchActiveAlerts(DEFAULT_PAGE);
  }, []);

  const calculatePercentage = useCallback((targetPrice) => {
    if (!currentPrice) return 0;
    const diff = ((targetPrice - currentPrice) / currentPrice) * 100;
    return diff.toFixed(1);
  }, [currentPrice]);

  const handleCreateAlert = () => {
    setSelectedAlert(null);
    setIsModalOpen(true);
  };

  const handleUpdateAlert = useCallback((alert) => {
    setSelectedAlert(alert);
    setIsModalOpen(true);
  }, []);

  const handleModalClose = () => {
    setIsModalOpen(false);
    fetchActiveAlerts(DEFAULT_PAGE); // Refresh list after modal close
  };

  const handleTabChange = (tab) => {
    setActiveTab(tab);

    // Reset scroll position to top
    const scrollDiv = document.getElementById('activeAlertScrollableDiv');
    if (scrollDiv) {
      scrollDiv.scrollTop = 0;
    }

    if (tab === 'active') {
      setPage({ pageNumber: 0 });
      setHasMoreActive(true);
      fetchActiveAlerts(DEFAULT_PAGE);
    } else if (tab === 'history') {
      setPastAlertPage({ pageNumber: 0 });
      setHasMorePast(true);
      fetchPastAlerts(DEFAULT_PAGE);
    }
  };

  useEffect(() => {
    if (activeTab === 'active') {
      fetchActiveAlerts(DEFAULT_PAGE);
    } else if (activeTab === 'history') {
      fetchPastAlerts(DEFAULT_PAGE);
    }
  }, [showOnlyCrypto, activeTab]);


  return (
    <div className='w-full h-full bg-gray-100 dark:bg-gray-800 flex flex-col'>

      <div className='p-4 flex items-center justify-between bg-gray-100 dark:bg-gray-700 cursor-pointer hover:bg-gray-200 dark:hover:bg-gray-600 transition-colors flex-shrink-0'
        onClick={() => setIsExpanded(!isExpanded)}
      >
        <div className='flex items-center gap-3'>
          <FaBell className='text-blue-400' size={18} />
          <span className='font-semibold text-gray-800 dark:text-white'>Price Alerts</span>
          <span className='px-3 py-1 rounded-full bg-blue-500 text-white text-xs font-medium'>{page?.totalElements || 0}</span>
        </div>
        <div className='flex items-center gap-6'>
          <button className='px-2 py-1 rounded-md bg-blue-500 hover:bg-blue-600 text-white font-medium text-sm flex items-center gap-2 transition-colors'
            onClick={(e) => {
              e.stopPropagation();
              handleCreateAlert();
            }}
          > Add Alert</button>
          {isExpanded ? (
            <FaChevronUp className='text-gray-400' />
          ) : (
            <FaChevronDown className='text-gray-400' />
          )}
        </div>
      </div>

      {isExpanded && (
        <>
          {/* tabs */}
          <div className='flex items-center bg-gray-100 dark:bg-gray-700 border-t border-gray-600 flex-shrink-0'>
            <div className="flex items-center space-x-2">
              <button className={`px-6 py-3 text-sm font-medium transition-colors ${activeTab === 'active' ? 'text-blue-400 border-b-2 border-blue-400' : 'text-gray-700 dark:text-gray-400 dark:hover:text-white'}`}
                onClick={() => handleTabChange('active')}
              >
                Active Alerts
              </button>
              {/*
            <button className={`px-6 py-3 text-sm font-medium transition-colors ${activeTab === 'triggered' ? 'text-blue-400 border-b-2 border-blue-400' : 'text-gray-700 dark:text-gray-400 dark:hover:text-white'}`}
              onClick={() => handleTabChange('triggered')}
            >
              Triggered Alerts
            </button>
            */}
              <button className={`px-6 py-3 text-sm font-medium transition-colors ${activeTab === 'history' ? 'text-blue-400 border-b-2 border-blue-400' : 'text-gray-700 dark:text-gray-400 dark:hover:text-white'}`}
                onClick={() => handleTabChange('history')}
              >
                Alerts History
              </button>
            </div>
            <div className="ml-auto me-5">
              <label className="flex items-center space-x-2 font-medium text-gray-700 dark:text-gray-300 text-sm cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={showOnlyCrypto}
                  onChange={(e) => setShowOnlyCrypto(e.target.checked)}
                  className="w-4 h-4 text-blue-500 bg-gray-800 border-gray-500 rounded focus:ring-blue-400 focus:ring-offset-0"
                />
                <span>{cryptoData.name} only</span>
              </label>
            </div>
          </div>
          <div id="activeAlertScrollableDiv" className='flex-1 overflow-y-auto p-5 bg-gray-200 dark:bg-gray-800 [&::-webkit-scrollbar]:w-2 [&::-webkit-scrollbar-track]:bg-gray-800 [&::-webkit-scrollbar-thumb]:bg-gray-600 [&::-webkit-scrollbar-thumb]:rounded [&::-webkit-scrollbar-thumb]:hover:bg-gray-500 [scrollbar-width:thin] [scrollbar-color:rgb(75_85_99)_rgb(31_41_55)]'>
            {/* Active Alerts */}
            {loading && (
              (activeTab === 'active' && activeAlerts.length === 0) ||
              (activeTab === 'history' && pastAlerts.length === 0)
            ) && <LoadingSpinner compact />}
            {activeTab === 'active' && (
              <InfiniteScroll
                key="active-scroll"
                scrollableTarget="activeAlertScrollableDiv"
                dataLength={activeAlerts.length}
                next={() => fetchActiveAlerts((page?.pageNumber ?? 0) + 1)}
                hasMore={hasMoreActive}             
              >
                <div className='space-y-3'>
                  {activeAlerts?.length === 0 ? (
                    <div className='text-center py-12 text-gray-700 dark:text-gray-400'>
                      <FaBell className='mx-auto mb-3 text-4xl opacity-50' />
                      <p>No active alerts</p>
                      <p className='text-sm'>Create your price alert</p>
                    </div>
                  ) : (
                    <>
                      {activeAlerts?.map(alert => (
                        <AlertCard
                          key={alert.id}
                          alert={alert}
                          handleUpdateAlert={handleUpdateAlert}
                          handleDeleteAlert={handleDeleteAlert}
                          calculatePercentage={calculatePercentage}
                          showOnlyCrypto={showOnlyCrypto}
                        />
                      ))}
                    </>
                  )}
                </div>
              </InfiniteScroll>
            )}
            {activeTab === 'history' && (
              <InfiniteScroll
                key="history-scroll"
                scrollableTarget="activeAlertScrollableDiv"
                dataLength={pastAlerts.length}
                next={() => fetchPastAlerts((pastAlertPage?.pageNumber ?? 0) + 1)}
                hasMore={hasMorePast}               
              >
                <div className='space-y-3'>
                  {pastAlerts?.length === 0 ? (
                    <div className='text-center py-12 text-gray-700 dark:text-gray-400'>
                      <FaBell className='mx-auto mb-3 text-4xl opacity-50' />
                      <p>No past alerts</p>
                      <p className='text-sm'>Your triggered alerts will appear here</p>
                    </div>
                  ) : (
                    <>
                      {pastAlerts?.map(alert => {
                        return (
                          <div key={alert.id} className='bg-gray-100 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-lg p-4 hover:border-blue-500 transition-all'>
                            <div className='flex items-center justify-between'>
                              <div className="w-[15%] flex items-center gap-2">
                                <img
                                  src={alert.cryptoImage}
                                  alt={alert.cryptoName}
                                  className="w-6 h-6 rounded-full"
                                />
                                <div className="flex flex-col">
                                  <h2 className="text-lg font-semibold text-gray-900 dark:text-white">{alert.cryptoName}</h2>
                                  <span className="text-sm font-semibold uppercase text-gray-600 dark:text-gray-300">
                                    {alert.cryptoSymbol}
                                  </span>
                                </div>
                              </div>
                              <div className='flex-1 ms-5'>
                                <div className="flex items-center mb-2 gap-4">
                                  {/* Left side: Alert name + condition */}
                                  <div className="flex items-center gap-2">
                                    <span className="text-gray-700 dark:text-gray-300 text-sm">
                                      {PRICE_CONDITIONS[alert.condition]}{" "}
                                      <span className="font-medium text-gray-700 dark:text-white">
                                        {formatPrice(alert.thresholdValue)}
                                      </span>
                                    </span>
                                  </div>
                                </div>

                                <div className='flex items-center gap-4 text-sm text-gray-400'>
                                  <div className='w-[30%] flex items-center gap-2'>
                                    {alert.notificationWebsocket && (
                                      <div className={`w-6 h-6 rounded flex items-center justify-center text-white ${alert.websocketSent ? "bg-green-500" : "bg-red-500"}`}>
                                        <FaBell size={12} />
                                      </div>
                                    )}
                                    {alert.notificationEmail && (
                                      <div className={`w-6 h-6 rounded flex items-center justify-center text-white ${alert.emailSent ? "bg-green-500" : "bg-red-500"}`}>
                                        <FaEnvelope size={12} />
                                      </div>
                                    )}
                                    {alert.notificationSms && (
                                      <div className={`w-6 h-6 rounded flex items-center justify-center text-white ${alert.smsSent ? "bg-green-500" : "bg-red-500"}`}>
                                        <FaSms size={12} />
                                      </div>
                                    )}
                                  </div>

                                </div>
                              </div>
                              <div className="flex items-center justify-between text-sm text-gray-700 dark:text-gray-300">
                                <span>
                                  <span className="text-gray-700 dark:text-gray-400">Created:</span>{" "}
                                  <span className="text-gray-700 dark:text-white font-medium">{formatDateTime(alert.createdAt)}</span>
                                </span>
                                <span className="mx-3 text-gray-600">|</span>
                                <span>
                                  <span className="text-gray-700 dark:text-gray-400">Triggered:</span>{" "}
                                  <span className="text-gray-700 dark:text-white font-medium">{formatDateTime(alert.triggeredAt)}</span>
                                </span>
                              </div>

                            </div>
                          </div>
                        )
                      })}
                    </>
                  )}
                </div>
              </InfiniteScroll>
            )}
          </div>
        </>
      )}

      {isModalOpen && (
        <AlertModal alert={selectedAlert} cryptoData={cryptoData} onClose={handleModalClose} />
      )}
    </div>
  );
}

const AlertCard = React.memo(({ alert, handleUpdateAlert, handleDeleteAlert, calculatePercentage, showOnlyCrypto }) => {

  const changePercentage = calculatePercentage(alert.thresholdValue)
  const isAbove = parseFloat(changePercentage) > 0;

  return (
    <div className='bg-gray-100 dark:bg-gray-700 border border-gray-200 dark:border-gray-600 rounded-lg p-4 hover:border-blue-700 dark:hover:border-blue-500 transition-all'>
      <div className='flex items-center justify-between'>
        <div className="w-[15%] flex items-center gap-2">
          <img
            src={alert.cryptoImage}
            alt={alert.cryptoName}
            className="w-6 h-6 rounded-full"
          />
          <div className="flex flex-col">
            <h2 className="text-lg font-semibold text-gray-900 dark:text-white">{alert.cryptoName}</h2>
            <span className="text-sm font-semibold uppercase text-gray-600 dark:text-gray-300">
              {alert.cryptoSymbol}
            </span>
          </div>
        </div>
        <div className='flex-1 ms-5'>
          <div className="flex items-center mb-2 gap-4">
            {/* Left side: Alert name + condition */}
            <div className="flex items-center gap-2">
              <span className="text-gray-600 dark:text-gray-300 text-sm">
                {PRICE_CONDITIONS[alert.condition]}{" "}
                <span className="font-medium text-gray-800 dark:text-white">
                  {formatPrice(alert.thresholdValue)}
                </span>
              </span>
            </div>

            {showOnlyCrypto && (
              < span
                className={`px-2 py-1 rounded text-xs font-medium whitespace-nowrap ${isAbove
                  ? "bg-green-500/20 text-green-400"
                  : "bg-red-500/20 text-red-400"
                  }`}
              >
                {Math.abs(changePercentage)}% {isAbove ? "above" : "below"}
              </span>
            )}
          </div>

          <div className='flex items-center gap-4 text-sm text-gray-400'>
            <div className='w-[15%] flex items-center gap-2'>
              {alert.notificationWebsocket && (
                <div className='w-6 h-6 rounded flex items-center justify-center bg-blue-500 text-white'>
                  <FaBell size={12} />
                </div>
              )}
              {alert.notificationEmail && (
                <div className='w-6 h-6 rounded flex items-center justify-center bg-blue-500 text-white'>
                  <FaEnvelope size={12} />
                </div>
              )}
              {alert.notificationSms && (
                <div className='w-6 h-6 rounded flex items-center justify-center bg-blue-500 text-white'>
                  <FaSms size={12} />
                </div>
              )}
            </div>
          </div>
        </div>

        <div className='flex items-center gap-4'>
          <div className="flex items-center justify-between me-5 text-sm text-gray-300">
            <span>
              <span className="text-gray-700 dark:text-gray-400">Created:</span>{" "}
              <span className="text-gray-600 dark:text-white font-medium">{formatDateTime(alert.createdAt)}</span>
            </span>
          </div>

          <button
            className='w-8 h-8 flex items-center justify-center text-blue-500 hover:text-blue-600 hover:bg-gray-200 dark:hover:bg-gray-600 rounded transition-colors'
            onClick={() => handleUpdateAlert(alert)}
          >
            <FaEdit size={16} />
          </button>
          <button className='w-8 h-8 flex items-center justify-center text-red-500 hover:text-red-600 hover:bg-gray-200 dark:hover:bg-gray-600 rounded transition-colors'
            onClick={() => handleDeleteAlert(alert.id)}
          >
            <FaTrash size={14} />
          </button>

        </div>
      </div>
    </div >
  )
})

export default AlertSection;

