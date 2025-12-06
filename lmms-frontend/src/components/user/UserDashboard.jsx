import { Button, Container } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

export default function UserDashboard() {
  const navigate = useNavigate();
  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };

  return (
    <Container className="mt-5">
      <h2>User Dashboard</h2>
      <p>Chào mừng USER!</p>
      <Button onClick={handleLogout}>Logout</Button>
    </Container>
  );
}
