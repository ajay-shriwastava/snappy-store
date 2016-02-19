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
package com.gemstone.gemfire;

import com.gemstone.gemfire.distributed.*;
//import com.gemstone.gemfire.*;
import com.gemstone.gemfire.internal.*;
import com.gemstone.gemfire.cache.*;
import junit.framework.*;
import java.util.Properties;
import java.io.*;
/**
 * This class makes sure that instantatiors are persisted to disk
 * and can be recovered.
 */
public class DiskInstantiatorsTest extends TestCase {

  public DiskInstantiatorsTest(String name) {
    super(name);
  }

  ////////  Test life cycle methods

  public void setUp() {
  }

  public void tearDown() {
  }

  private DistributedSystem ds = null;
  private Cache c = null;
  private Region r = null;

  private void connect() throws CacheException {
//    Cache c = null;
    Properties cfg = new Properties();
    cfg.setProperty("mcast-port", "0");
    cfg.setProperty("locators", "");
    cfg.setProperty("statistic-sampling-enabled", "false");

    this.ds = DistributedSystem.connect(cfg);
    this.c = CacheFactory.create(ds);
    AttributesFactory factory = new AttributesFactory();
    factory.setScope(Scope.LOCAL);
    factory.setDataPolicy(DataPolicy.PERSISTENT_REPLICATE);
    factory.setDiskSynchronous(true);
    factory.setDiskStoreName(this.c.createDiskStoreFactory()
                             .create("DiskInstantiatorsTest")
                             .getName());
      
    this.r = this.c.createRegion("DiskInstantiatorsTest",
                                   factory.create());
  }
  
  private void disconnect() throws CacheException {
    this.r = null;
    if (this.c != null) {
      this.c.close();
      this.c = null;
    }
    if (this.ds != null) {
      this.ds.disconnect();
      this.ds = null;
    }
  }
  
  public void testDiskInstantiators() throws CacheException {
    try {
      connect();
      int size = this.r.entries(false).size();
      if (size != 0) {
        fail("expected 0 entries but had " + size);
      }
      this.ds.getLogWriter().info("adding entry");
      r.put(new Key(1), new Payload(100));
      disconnect();
      // now unregister and make sure we can restore
      InternalInstantiator.unregister(Payload.class, (byte)22);
      InternalInstantiator.unregister(Key.class, (byte)21);
      connect();
      size = this.r.entries(false).size();
      if (size != 1) {
        fail("expected 1 entry but had " + size);
      }
      Object value = r.get(new Key(1));
      this.ds.getLogWriter().info("found entry");
      if (!(value instanceof Payload)) {
        fail("Expected value to be an instance of Payload but it was "
             + value.getClass());
      }
      disconnect();
    } finally {
      try {
        if (this.ds == null) {
          connect();
        }
        if (this.r != null) {
          this.ds.getLogWriter().info("destroying region");
          this.r.localDestroyRegion();
        }
      } finally {
        disconnect();
      }
    }
  }

  static class Payload implements DataSerializable {
    static {
      Instantiator.register(new Instantiator(Payload.class, (byte) 22) {
          public DataSerializable newInstance() {
            return new Payload();
          }
        });
    }
    private byte[] data;
    public Payload() {
    }
    public Payload(int size) {
      this.data = new byte[size];
    }
    public void toData(DataOutput dataOutput) throws IOException  {
      DataSerializer.writeByteArray(this.data, dataOutput);
    }
    public void fromData(DataInput dataInput) throws IOException {
      this.data = DataSerializer.readByteArray(dataInput);
    }
  }
  static class Key implements DataSerializable {
    static {
      Instantiator.register(new Instantiator(Key.class, (byte) 21) {
          public DataSerializable newInstance() {
            return new Key();
          }
        });
    }

    public int hashCode() {
      return this.key.hashCode();
    }
    public boolean equals(Object obj) {
      if (obj instanceof Key) {
        return this.key.equals(((Key)obj).key);
      } else {
        return false;
      }
    }
    private Long key;
    public Key() {
    }
    public Key(long k) {
      this.key = new Long(k);
    }
    public void toData(DataOutput dataOutput) throws IOException  {
      dataOutput.writeLong(this.key.longValue());
    }
    public void fromData(DataInput dataInput) throws IOException {
      this.key = new Long(dataInput.readLong());
    }
  }
}