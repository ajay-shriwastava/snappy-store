/*
 * Copyright (c) 2018 SnappyData, Inc. All rights reserved.
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
package com.gemstone.gemfire.internal.snappy;

import com.gemstone.gemfire.internal.shared.ByteBufferReference;

/**
 * Encapsulates a row read from column store.
 */
public final class ColumnTableEntry {
  public final long uuid;
  public final int bucketId;
  public final int columnPosition;
  public final ByteBufferReference columnValue;

  public ColumnTableEntry(long uuid, int bucketId, int columnPosition,
      ByteBufferReference columnValue) {
    this.uuid = uuid;
    this.bucketId = bucketId;
    this.columnPosition = columnPosition;
    this.columnValue = columnValue;
  }
}