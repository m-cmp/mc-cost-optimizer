import { RouterProvider } from "react-router-dom";
import { router } from "../src/routes/router";
import PostMessageListener from "./features/PostMessageListener";

function App() {
  return (
    <div className="wrap">
      <PostMessageListener />
      <RouterProvider router={router} />
    </div>
  );
}

export default App;
