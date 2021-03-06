/*

   Derby - Class com.pivotal.gemfirexd.internal.impl.sql.compile.AccessPathImpl

   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to you under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */

package	com.pivotal.gemfirexd.internal.impl.sql.compile;



import com.pivotal.gemfirexd.internal.iapi.error.StandardException;
import com.pivotal.gemfirexd.internal.iapi.reference.SQLState;
import com.pivotal.gemfirexd.internal.iapi.services.sanity.SanityManager;
import com.pivotal.gemfirexd.internal.iapi.sql.compile.AccessPath;
import com.pivotal.gemfirexd.internal.iapi.sql.compile.CostEstimate;
import com.pivotal.gemfirexd.internal.iapi.sql.compile.JoinStrategy;
import com.pivotal.gemfirexd.internal.iapi.sql.compile.Optimizer;
import com.pivotal.gemfirexd.internal.iapi.sql.dictionary.ConglomerateDescriptor;
import com.pivotal.gemfirexd.internal.iapi.sql.dictionary.ConstraintDescriptor;
import com.pivotal.gemfirexd.internal.iapi.sql.dictionary.DataDictionary;
import com.pivotal.gemfirexd.internal.iapi.sql.dictionary.TableDescriptor;

class AccessPathImpl implements AccessPath
{
	ConglomerateDescriptor	cd = null;
	private CostEstimate	costEstimate = null;
	boolean					coveringIndexScan = false;
	boolean					nonMatchingIndexScan = false;
	JoinStrategy			joinStrategy = null;
	int						lockMode;
	Optimizer				optimizer;
	private String			accessPathName = "";
	private boolean supportsMoveToNextKey = false;

	AccessPathImpl(Optimizer optimizer)
	{
		this.optimizer = optimizer;
	}

	/** @see AccessPath#setConglomerateDescriptor */
	public void setConglomerateDescriptor(ConglomerateDescriptor cd)
	{
		this.cd = cd;
	}

	/** @see AccessPath#getConglomerateDescriptor */
	public ConglomerateDescriptor getConglomerateDescriptor()
	{
		return cd;
	}

	/** @see AccessPath#setCostEstimate */
	public void setCostEstimate(CostEstimate costEstimate)
	{
		/*
		** CostEstimates are mutable, so keep the best cost estimate in
		** a copy.
		*/
		if (this.costEstimate == null)
		{
			if (costEstimate != null)
			{
				this.costEstimate = costEstimate.cloneMe();
			}
		}
		else
		{
			if (costEstimate == null)
				this.costEstimate = null;
			else
				this.costEstimate.setCost(costEstimate);
		}
	}

	/** @see AccessPath#getCostEstimate */
	public CostEstimate getCostEstimate()
	{
		return costEstimate;
	}

	/** @see AccessPath#setCoveringIndexScan */
	public void setCoveringIndexScan(boolean coveringIndexScan)
	{
		this.coveringIndexScan = coveringIndexScan;
	}

	/** @see AccessPath#getCoveringIndexScan */
	public boolean getCoveringIndexScan()
	{
		return coveringIndexScan;
	}

	/** @see AccessPath#setNonMatchingIndexScan */
	public void setNonMatchingIndexScan(boolean nonMatchingIndexScan)
	{
		this.nonMatchingIndexScan = nonMatchingIndexScan;
	}

	/** @see AccessPath#getNonMatchingIndexScan */
	public boolean getNonMatchingIndexScan()
	{
		return nonMatchingIndexScan;
	}

	/** @see AccessPath#setJoinStrategy */
	public void setJoinStrategy(JoinStrategy joinStrategy)
	{
		this.joinStrategy = joinStrategy;
	}

	/** @see AccessPath#getJoinStrategy */
	public JoinStrategy getJoinStrategy()
	{
		return joinStrategy;
	}

	/** @see AccessPath#setLockMode */
	public void setLockMode(int lockMode)
	{
		this.lockMode = lockMode;
	}

	/** @see AccessPath#getLockMode */
	public int getLockMode()
	{
		return lockMode;
	}

	/** @see AccessPath#copy */
	public void copy(AccessPath copyFrom)
	{
		setConglomerateDescriptor(copyFrom.getConglomerateDescriptor());
		setCostEstimate(copyFrom.getCostEstimate());
		setCoveringIndexScan(copyFrom.getCoveringIndexScan());
		setNonMatchingIndexScan(copyFrom.getNonMatchingIndexScan());
		setJoinStrategy(copyFrom.getJoinStrategy());
		setLockMode(copyFrom.getLockMode());
		setSupportsMoveToNextKey(copyFrom.getFlagSupportsMoveToNextKey());
	}

	/** @see AccessPath#getOptimizer */
	public Optimizer getOptimizer()
	{
		return optimizer;
	}

	public String toString()
	{
		if (SanityManager.DEBUG)
		{
			return "cd == " + cd +
				", costEstimate == " + costEstimate +
				", coveringIndexScan == " + coveringIndexScan +
				", nonMatchingIndexScan == " + nonMatchingIndexScan +
				", joinStrategy == " + joinStrategy +
				", lockMode == " + lockMode +
				", optimizer level == " + optimizer.getLevel();
		}
		else
		{
			return "";
		}
	}
	
	/** @see AccessPath#initializeAccessPathName */
	public void initializeAccessPathName(DataDictionary dd, TableDescriptor td)
	       throws StandardException
	{
		if (cd == null)
			return;

		if (cd.isConstraint())
		{
			ConstraintDescriptor constraintDesc = 
				dd.getConstraintDescriptor(td, cd.getUUID());
			if (constraintDesc == null)
			{
				throw StandardException.newException(
										SQLState.LANG_OBJECT_NOT_FOUND,
										"CONSTRAINT on TABLE",
										td.getName());
			}
			accessPathName = constraintDesc.getConstraintName();
		} 
		else if (cd.isIndex())
		{
			accessPathName = cd.getConglomerateName();
		} 
		else 
		{
			accessPathName = "";
		} 
	}
	
	public void setSupportsMoveToNextKey(boolean supports) {
		this.supportsMoveToNextKey  = supports;
	}
	public boolean getFlagSupportsMoveToNextKey() {
		return this.supportsMoveToNextKey;
	}
}
