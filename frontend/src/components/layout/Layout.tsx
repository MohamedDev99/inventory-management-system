import { Outlet, Link } from 'react-router-dom';
import {
    LayoutDashboard,
    Package,
    Warehouse,
    ShoppingCart,
    FileText
} from 'lucide-react';

const Layout = () => {
    return (
        <div className="flex h-screen bg-gray-100">
            {/* Sidebar */}
            <aside className="w-64 bg-gray-900 text-white">
                <div className="p-4">
                    <h1 className="text-xl font-bold">Inventory System</h1>
                </div>

                <nav className="mt-8">
                    <NavLink to="/dashboard" icon={<LayoutDashboard size={20} />}>
                        Dashboard
                    </NavLink>
                    <NavLink to="/products" icon={<Package size={20} />}>
                        Products
                    </NavLink>
                    <NavLink to="/inventory" icon={<Warehouse size={20} />}>
                        Inventory
                    </NavLink>
                    <NavLink to="/orders" icon={<ShoppingCart size={20} />}>
                        Orders
                    </NavLink>
                    <NavLink to="/reports" icon={<FileText size={20} />}>
                        Reports
                    </NavLink>
                </nav>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-auto">
                <header className="bg-white shadow-sm p-4">
                    <div className="flex justify-between items-center">
                        <h2 className="text-2xl font-semibold">Welcome</h2>
                        <button className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">
                            Profile
                        </button>
                    </div>
                </header>

                <div className="p-6">
                    <Outlet />
                </div>
            </main>
        </div>
    );
};


const NavLink = ({ to, icon, children }: { to: string; icon: React.ReactNode; children: React.ReactNode; }) => (
    <Link
        to={to}
        className="flex items-center gap-3 px-4 py-3 hover:bg-gray-800 transition-colors"
    >
        {icon}
        <span>{children}</span>
    </Link>
);

export default Layout;