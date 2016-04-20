package controllers
import com.restfb.DefaultFacebookClient



object graph_analysis {
    
    
    
    def g_analysis(topArray : Array[(String, String)], fbClient:DefaultFacebookClient, id:String) = {

    
    val scoreArray = topArray.map { x => x._1}
    val idArray = topArray.map { x => x._2}
    
    // generate graph
    val name = "name"
   // likes_graph.createGraph(id, name, fbClient)
    

    val idCountArray = idArray.map { x => likes_graph.getSimCount(id,x)+1 }

    

   
    val toplist = for 
                   {
      
      
                        idc  <-  idCountArray
       
                         top   <- topArray
      
                         val a = (parseDouble(top._1))
                       
                         val weightedScore = a.get * idc
      
                          val tuple = (weightedScore,top._2)
                          
                    } yield tuple
      
              
                val sublist = topArray.filter{ p => {parseDouble(p._1) == None}}
                    
                    
                    
                    
    toplist.toList.sortWith(_._1 > _._1)

    
    val out_list = (toplist++sublist).map(f => f._2)
    
   
        out_list
        
    }
    
    
    
    def parseDouble(s: String) = try { Some(s.toDouble) } catch { case _ => None }
    
    
}
