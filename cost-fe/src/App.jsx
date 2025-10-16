import { RouterProvider } from "react-router-dom";
import { router } from "../src/routes/router";
import PostMessageListener from "./features/PostMessageListener";
import AlertProvider from "./components/common/alert/AlertProvider";

function App() {
  return (
    <div className="wrap">
      <PostMessageListener />
      <AlertProvider />
      <RouterProvider router={router} />
    </div>
  );
}

export default App;
