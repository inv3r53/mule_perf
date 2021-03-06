# mule_perf

<?xml version="1.0" encoding="UTF-8"?>

<mule xmlns:tcp="http://www.mulesoft.org/schema/mule/tcp" xmlns:tls="http://www.mulesoft.org/schema/mule/tls" xmlns:scripting="http://www.mulesoft.org/schema/mule/scripting"
	xmlns:http="http://www.mulesoft.org/schema/mule/http" xmlns="http://www.mulesoft.org/schema/mule/core"
	xmlns:doc="http://www.mulesoft.org/schema/mule/documentation"
	xmlns:spring="http://www.springframework.org/schema/beans" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.mulesoft.org/schema/mule/scripting http://www.mulesoft.org/schema/mule/scripting/current/mule-scripting.xsd
http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-current.xsd
http://www.mulesoft.org/schema/mule/core http://www.mulesoft.org/schema/mule/core/current/mule.xsd
http://www.mulesoft.org/schema/mule/http http://www.mulesoft.org/schema/mule/http/current/mule-http.xsd
http://www.mulesoft.org/schema/mule/tls http://www.mulesoft.org/schema/mule/tls/current/mule-tls.xsd
http://www.mulesoft.org/schema/mule/tcp http://www.mulesoft.org/schema/mule/tcp/current/mule-tcp.xsd">
	<http:listener-config name="HTTP_Listener_Configuration2"
		host="0.0.0.0" port="8090" doc:name="HTTP Listener Configuration" />
	<asynchronous-processing-strategy
		name="Asynchronous_Processing_Strategy" maxThreads="5000"
		doc:name="Asynchronous Processing Strategy" />
    <http:request-config name="HTTP_Request_Configuration2" host="meldev1fstex01.sms.foxtel.com.au" port="9002" doc:name="HTTP Request Configuration"   responseTimeout="30000" basePath="/iamwebservices/AccountService" protocol="HTTPS">
        <tls:context>
            <tls:trust-store insecure="true"/>
        </tls:context>
    </http:request-config>
	<flow name="stub-parallel-requestsFlow">
		<http:listener config-ref="HTTP_Listener_Configuration2"
			path="/" doc:name="HTTP" />
        <async doc:name="Async">
            <flow-ref name="stub-parallel-requestsSub_Flow" doc:name="stub-parallel-requestsSub_Flow"/>
        </async>
		<set-payload value="#[message.id]" doc:name="Set Payload" />
	</flow>
	<sub-flow name="stub-parallel-requestsSub_Flow">
		<logger message="#[message.id] Commence Parallel Tests" level="INFO"
			doc:name="Log Start Parallel Tests" />
		<scripting:component doc:name="Create Iterable Test Case">
			<scripting:script engine="Groovy"><![CDATA[payload = new java.util.ArrayList();
for (int i=0; i < 100; i++) {
     String request = "REQ-"+i;
     payload.add(request);
}
return payload;]]></scripting:script>
		</scripting:component>
		<foreach doc:name="For Each">
            <async processingStrategy="Asynchronous_Processing_Strategy" doc:name="Async">
                <flow-ref name="Single-Thread" doc:name="Single-Thread"/>
            </async>
		</foreach>
	</sub-flow>
	<sub-flow name="Single-Thread">
		<set-variable variableName="threadName" value="#['T-ONE-'+payload]"
			doc:name="Variable - ThreadName" />
		<set-variable variableName="startTime" value="#[new java.util.Date()]"
			doc:name="Set Start Time" />
        <parse-template location="soap_sample.xml" doc:name="Parse Template"/>
        <http:request config-ref="HTTP_Request_Configuration2" path="/" method="POST" doc:name="HTTP - Call AccountService SOAP">
            <http:request-builder>
                <http:header headerName="soapAction" value="GetAccount"/>
                <http:header headerName="Content-Type" value="application/xml"/>

            </http:request-builder>
        </http:request>
        <object-to-string-transformer doc:name="Object to String"/>

		<set-variable variableName="endTime" value="#[new java.util.Date()]"
			doc:name="Set End Time" />
		<set-variable variableName="timeElapsed"
			value="#[flowVars.endTime.getTime() - flowVars.startTime.getTime()]"
			doc:name="Determine the Time Elapsed" />
		<logger
			message="#[server.dateTime],#[flowVars.threadName], Elapsed Time, #[flowVars.timeElapsed], #[message.inboundProperties.'http.status']"
			level="INFO" doc:name="Log - Thread Completion" />
	</sub-flow>
</mule>

