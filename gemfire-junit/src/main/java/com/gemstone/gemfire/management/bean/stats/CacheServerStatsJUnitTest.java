/*
 * Copyright (c) 2010-2015 Pivotal Software, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. See accompanying
 * LICENSE file.
 */
package com.gemstone.gemfire.management.bean.stats;

import com.gemstone.gemfire.cache.server.ServerLoad;
import com.gemstone.gemfire.internal.cache.tier.sockets.CacheServerStats;
import com.gemstone.gemfire.management.internal.beans.CacheServerBridge;

/**
 * @author rishim
 */
public class CacheServerStatsJUnitTest extends MBeanStatsTestCase {

  private CacheServerBridge bridge;

  private CacheServerStats cacheServerStats;

  public CacheServerStatsJUnitTest(String name) {
    super(name);
  }

  public void init() {
    cacheServerStats = new CacheServerStats("Test Sock Name");
    
    bridge = new CacheServerBridge();
    bridge.addCacheServerStats(cacheServerStats);
  }
  
  public void testServerStats() throws InterruptedException{
    long startTime = System.currentTimeMillis();
    
    cacheServerStats.incCurrentClients();
    cacheServerStats.incConnectionThreads();
    cacheServerStats.incThreadQueueSize();
    cacheServerStats.incCurrentClientConnections();
    cacheServerStats.incFailedConnectionAttempts();
    cacheServerStats.incConnectionsTimedOut();
    cacheServerStats.incReadQueryRequestTime(startTime);
    cacheServerStats.incSentBytes(20);
    cacheServerStats.incReceivedBytes(20);
    cacheServerStats.incReadGetRequestTime(startTime);
    cacheServerStats.incProcessGetTime(startTime);
    cacheServerStats.incReadPutRequestTime(startTime);
    cacheServerStats.incProcessPutTime(startTime);
    
    ServerLoad load = new ServerLoad(1,1,1,1);
    cacheServerStats.setLoad(load);
    
    sample();
    
    assertEquals(1, getCurrentClients());
    assertEquals(1, getConnectionThreads());
    assertEquals(1, getThreadQueueSize());
    assertEquals(1, getClientConnectionCount());
    assertEquals(1, getTotalFailedConnectionAttempts());
    assertEquals(1, getTotalConnectionsTimedOut());
    assertEquals(20, getTotalSentBytes());
    assertEquals(20, getTotalReceivedBytes());
    
    assertEquals(1.0,getConnectionLoad());
    assertEquals(1.0,getLoadPerConnection());
    assertEquals(1.0,getLoadPerQueue());
    assertEquals(1.0,getQueueLoad());
    
    assertTrue(getQueryRequestRate() >0);
    assertTrue(getGetRequestRate() >0);
    assertTrue(getGetRequestAvgLatency() >0);
    assertTrue(getPutRequestRate() >0);
    assertTrue(getPutRequestAvgLatency() >0);
    
    bridge.stopMonitor();
    
    assertEquals(0, getCurrentClients());
    assertEquals(0, getConnectionThreads());
  }

  private double getConnectionLoad() {
    return bridge.getConnectionLoad();
  }

  private int getConnectionThreads() {
    return bridge.getConnectionThreads();
  }

  private long getGetRequestAvgLatency() {
    return bridge.getGetRequestAvgLatency();
  }

  private float getGetRequestRate() {
    return bridge.getGetRequestRate();
  }

  private long getPutRequestAvgLatency() {
    return bridge.getPutRequestAvgLatency();
  }

  private float getPutRequestRate() {
    return bridge.getPutRequestRate();
  }

  private double getLoadPerConnection() {
    return bridge.getLoadPerConnection();
  }

  private double getLoadPerQueue() {
    return bridge.getLoadPerQueue();
  }

  private float getQueryRequestRate() {
    return bridge.getQueryRequestRate();
  }

  private double getQueueLoad() {
    return bridge.getQueueLoad();
  }

  private int getThreadQueueSize() {
    return bridge.getThreadQueueSize();
  }

  private int getTotalConnectionsTimedOut() {
    return bridge.getTotalConnectionsTimedOut();
  }

  private int getTotalFailedConnectionAttempts() {
    return bridge.getTotalFailedConnectionAttempts();
  }

  private long getTotalIndexMaintenanceTime() {
    return bridge.getTotalIndexMaintenanceTime();
  }

  private long getTotalSentBytes() {
    return bridge.getTotalSentBytes();
  }

  private long getTotalReceivedBytes() {
    return bridge.getTotalReceivedBytes();
  }

  private int getClientConnectionCount() {
    return bridge.getClientConnectionCount();
  }

  private int getCurrentClients() {
    return bridge.getCurrentClients();
  }
}
