const Dashboard = () => {
    return (
        <div>
            <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <StatCard title="Total Products" value="1,234" />
                <StatCard title="Total Value" value="$45,678" />
                <StatCard title="Low Stock Items" value="23" />
                <StatCard title="Pending Orders" value="12" />
            </div>
        </div>
    );
};

const StatCard = ({ title, value }: { title: string; value: string }) => (
    <div className="bg-white p-6 rounded-lg shadow">
        <h3 className="text-gray-500 text-sm">{title}</h3>
        <p className="text-3xl font-bold mt-2">{value}</p>
    </div>
);

export default Dashboard;