package com.zpq.web.util;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class IdUtils {

	private static final AtomicInteger auditGen = new AtomicInteger(1000);
	private static final AtomicInteger accountGen = new AtomicInteger(1000);
	// private static final AtomicInteger flowGen = new AtomicInteger(0);
	private static final ConcurrentHashMap<String, AtomicInteger> date2flow = new ConcurrentHashMap<>();

	/**
	 * 根据时间生成唯一ID，不会重复<br>
	 * 每秒最多支持99999个ID <br>
	 * ---------yyyy_MM_dd_HH_mm_ss<br>
	 * long a = 2018_02_01_10_04_27_10000L;
	 * 
	 * @return
	 */
	public static long genId() {

		String dateStr = DateUtils.format(new Date(), "yyyyMMddHHmmss");

		int autoId = 0;
		AtomicInteger ai = date2flow.get(dateStr);
		if (ai == null) {
			synchronized (date2flow) {
				ai = date2flow.get(dateStr);
				if (ai == null) {
					date2flow.clear();
					ai = new AtomicInteger(0);
					date2flow.put(dateStr, ai);
					autoId = ai.incrementAndGet();
				} else {
					autoId = ai.incrementAndGet();
				}
			}
		} else {
			autoId = ai.incrementAndGet();
		}
		if (autoId > 9_9999) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return genId();
		}
		Long result = Long.valueOf(dateStr + autoId);

		return result;
	}
	
	public static void main(String[] args) {
		
		int threadnum = 20;
		final CountDownLatch cdl = new CountDownLatch(threadnum);
		
		final ConcurrentHashMap<Long, Long> idmap = new ConcurrentHashMap<>();
		for(int i = 0; i< threadnum; i++) {
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					for (int j = 0; j < 10_0000; j++) {
						long id = genId();
						Long old = idmap.putIfAbsent(id, id);
						if(old != null) {
							System.err.println("----------------");
						}
					}
					
					cdl.countDown();
				}
			});
			
			t.start();
		}
		
		try {
			cdl.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println(idmap.size());
		
	}

	/**
	 * 获取审核ID
	 * 
	 * @param type
	 * @return
	 */
	public static String getAuditId(String prefix) {
		String auditId = prefix;
		String dateStr = DateUtils.format(new Date(), "yyyyMMddHHmmss");
		auditId += dateStr;
		int incre = 0;
		synchronized (auditGen) {
			if (auditGen.get() > 9998) {
				auditGen.set(1000);
			}
			incre = auditGen.incrementAndGet();
		}
		auditId += incre;
		return auditId;
	}

	public static String genAccount(String prefix) {
		StringBuilder result = new StringBuilder();
		result.append(prefix);
		String dateStr = DateUtils.format(new Date(), "yyyyMMddHHmmss");
		result.append(dateStr);
		int incre = 0;
		synchronized (accountGen) {
			if (accountGen.get() > 9998) {
				accountGen.set(1000);
			}
			incre = auditGen.incrementAndGet();
		}
		result.append(incre);
		return result.toString();
	}

}
