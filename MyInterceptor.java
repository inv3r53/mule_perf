package com.mulesoft.training;

import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.interceptor.AbstractEnvelopeInterceptor;
import org.mule.management.stats.ProcessingTime;

public class MyInterceptor extends AbstractEnvelopeInterceptor {

	@Override
	public MuleEvent before(MuleEvent event) throws MuleException {
		// TODO Auto-generated method stub
//		System.out.println("\n\nIn start of Ibnterceptor");
//		try {
//			System.out.println(event.getMessage().getPayloadAsString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return event;
	}

	@Override
	public MuleEvent after(MuleEvent event) throws MuleException {
		// TODO Auto-generated method stub
		return event;
	}

	@Override
	public MuleEvent last(MuleEvent event, ProcessingTime time, long startTime, boolean exceptionWasThrown)
			throws MuleException {
		// TODO Auto-generated method stub

		System.out.println("\n\n\n\n my Interceptor did it\n\n\n");

		System.out.println("Hello");
		try {
			
			System.out.println(event.getMessageSourceURI()+"\n\n");
			System.out.println(event.getFlowCallStack().toString());

			System.out.println(event.getMessage().getPayloadAsString());
			
			System.out.println(event.getMuleContext().getClusterId());
			
			System.out.println(event.getMuleContext().getClusterNodeId());
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return event;
	}

}
