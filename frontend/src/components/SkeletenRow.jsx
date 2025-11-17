const SkeletonRow = ({ isMobile }) => {

    if (isMobile) {

        return (
            <div className="bg-white dark:bg-gray-800 p-4 rounded-lg shadow animate-pulse flex flex-col gap-3">
                <div className="flex items-center gap-3 mb-3">
                    <div className="w-6 h-6 bg-gray-300 dark:bg-gray-600 rounded-full"></div>
                    <div className="flex-flex-col gap-1">
                        <div className="w-20 h-4 bg-gray-300 dark:bg-gray-600 rounded"></div>
                        <div className="w-12 h-3 bg-gray-300 dark:bg-gray-600 rounded"></div>
                    </div>
                </div>
                <div className="flex-flex-col gap-1">
                    <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded"></div>
                    <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded"></div>
                    <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded"></div>
                    <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded"></div>
                </div>
            </div>
        )
    }

    return (

        <tr>
            <td className="px-2 py-3">
                <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
            </td>
            <td className="px-2 py-3 flex items-center gap-2">
                <div className="w-6 h-6 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
                <div className="min-w-[80px] flex flex-col gap-1">
                    <div className="w-16 h-4 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
                    <div className="w-12 h-3 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
                </div>
            </td>
            <td className="px-2 py-3">
                <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
            </td>
            <td className="px-2 py-3">
                <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
            </td>
            <td className="px-2 py-3">
                <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
            </td>
            <td className="px-2 py-3">
                <div className="w-full h-3 bg-gray-300 dark:bg-gray-600 rounded animate-pulse"></div>
            </td>
        </tr>
    )

}

export default SkeletonRow;