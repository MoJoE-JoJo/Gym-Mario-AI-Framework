from py4j.java_gateway import (
    JavaGateway, CallbackServerParameters, GatewayParameters,
    launch_gateway)
# launch Java side with dynamic port and get back the port on which the
# server was bound to.
port = launch_gateway(jarpath="./Mario_AI_Framework/out/artifacts/MarioGym_jar/Mario_AI_Framework.jar")

# connect python side to Java side with Java dynamic port and start python
# callback server with a dynamic port
gateway = JavaGateway(
    gateway_parameters=GatewayParameters(port=port),
    callback_server_parameters=CallbackServerParameters(port=0))

# retrieve the port on which the python callback server was bound to.
python_port = gateway.get_callback_server().get_listening_port()

# tell the Java side to connect to the python callback server with the new
# python port. Note that we use the java_gateway_server attribute that
# retrieves the GatewayServer instance.
gateway.java_gateway_server.resetCallbackClient(
    gateway.java_gateway_server.getCallbackClient().getAddress(),
    python_port)
