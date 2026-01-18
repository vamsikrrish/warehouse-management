# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
1. The different operations and costs involved for each of the operation are not listed down in the requirements. 
2. Defining the right level of cost granularity (warehouse, store, or order). Assumin the costs are at warehouse level for simplicity.
3. With the above assumtpion, Operations can be associated with the respective tasks and we can have an operation transaction table where the operations performed are all tracked against each operation at warehouse level. so that the same can be traversed back for cost allocation and tracking

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
1. Need an understanding of how costs are calculated
2. Need a historical data for any given warehouse to proceed with identifying the top spent areas
3. Provide optimal routes/minimal set of operations to perform any given action based on historical data or optimized algorithms
4. Avoid unncesessary movements/operations which might increase overall cost

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
1. Integration with financial systems gives transparency and single source of truth
2. For realtime synchronization, we can use event based message queues like apache kafka/rabbit MQ with retry based failure handling

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
1. We can use the historical data and integrate with LLM models to identify specific trends in the costs per warehouse and generate a forecase based on that

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
1. The old warehouse is always archieved and not deleted from the system. 
2. The old warehouse has an archievedAt column which specifies when a particular warehouse was decommissioned, which can be used to traceback to the operations - cost transactions table using which the history can be preserved

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
