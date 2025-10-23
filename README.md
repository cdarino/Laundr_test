ts basically laundr but in dankada

WIP: Added Service Factory & Order Facade integration (GUI link pending)

WIP: Implement Creational & Structural patterns integration (Steps 1–2 done, Step 3 pending)

Summary:
- Added org.example.service package with Factory Method pattern (ServiceFactory + concrete services).
- Implemented OrderProcessingFacade to simulate full order workflow (pricing, payment, notifications).
- Added initial Builder integration for LaundryOrder creation.
- Updated ServiceCard to support click events (preparing for GUI selection handling).
- Confirmed working console output for Factory + Facade integration.
- Repository compiles and runs successfully.

Pattern Progress:
✅ Factory Method – Implemented (WashAndFoldService, DryCleanService, PressOnlyService)
✅ Builder – Functional backend builder created
✅ Facade – Working OrderProcessingFacade (console tested)
⚙️ Observer – Console notifications simulated, GUI integration pending
❌ State – Not yet implemented
❌ Decorator – Not yet implemented

Next Steps:
1. Complete Step 3 – Hook ServiceCard selections to LaundromatDetailsPanel.
2. Add checkout fields for pickup/delivery/contact and connect to OrderProcessingFacade.
3. Implement State pattern for LaundryOrder lifecycle (Placed → PickedUp → Delivered).
4. Add proper Observer pattern for customer notifications in GUI.
5. Implement Decorator pattern for add-on services (StainRemoval, FabricSoftener, ExpressDelivery).

Note:
Current GUI not yet connected to backend order system.
Safe to build and run; backend integration tested successfully.


(could you tell)
