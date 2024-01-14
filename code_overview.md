Every item has a 
    -name 
    -source: Source 

Source (of item): has types:
- JobSource 
- MerchantSource
- DropSource

JobSource: has field: 
- Producer
  - Profession 
  - Level


Producer: has fields 
-Profession 
-Level 

JobSource: has types:
- GatheringSource 
- CraftingSource 

GatheringSource: has fields 
-  producer: GatheringProducer

           Profession
           Level
       GatheringSource
           gatheringProducer
           Location?
       CraftingSource 
            
           Recipe? 

   Merchant
   Drop

GatheringProducer : Producer  
    GatheringProfession
    Level

CraftingProducer: Producer 
    CraftingProfession
    Level