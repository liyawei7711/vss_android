
var deviceTop = {
"nResultCode": 0,
"strResultDescribe": "操作成功",
"lstMeshDeviceTop": [{
		                 "srcdev":	{
					"strIP":	"192.168.2.1",
					"strByname":"1#",
					"nNoise":-105,
					"strMac":"1dsad",
"nDevType":0
				},
				"desdev":	{
					"strIP":	"192.168.2.1",
					"strByname":"3#",
					"nNoise":-99,
					"strMac":"1dsad",
"nDevType":0
				},
"nSnr":42,
"nQuality":42,
"nNoise":42,
"nSignal":42,
"nRecvMode":42,
"nSendMode":42
			}, {                                  
                                "srcdev":       {              
                                        "strIP":"192.168.2.3",
                                        "strByname":"3#", 
                                        "nNoise":-99,
				     "strMac":"1dsad"
   
                                },                            
                                "desdev":       {           
                                        "strIP":   "192.168.2.1",
                                        "strByname": "1#", 
                                        "nNoise": -105,
				     "strMac":"1dsad"
},                                 "nSnr":42,
"nQuality":42,
"nNoise":42,
"nSignal":42,
"nRecvMode":42,
"nSendMode":42
                   
                        }, {                                  
                                "srcdev":       {              
                                        "strIP":"192.168.2.3",
                                        "strByname":"3#", 
                                        "nNoise":-99,
				     "strMac":"1dsad"
   
                                },                            
                                "desdev":       {           
                                        "strIP":   "192.168.2.4",
                                        "strByname": "1#", 
                                        "nNoise": -105,
				     "strMac":"1dsad"
},                                 "nSnr":42,
"nQuality":42,
"nNoise":42,
"nSignal":42,
"nRecvMode":42,
"nSendMode":42
                   
                        }, {                                  
                                "srcdev":       {              
                                        "strIP":"192.168.2.5",
                                        "strByname":"3#", 
                                        "nNoise":-99,
				     "strMac":"1dsad"
   
                                },                            
                                "desdev":       {           
                                        "strIP":   "192.168.2.4",
                                        "strByname": "1#", 
                                        "nNoise": -105,
				     "strMac":"1dsad"
},                                 "nSnr":42,
"nQuality":42,
"nNoise":42,
"nSignal":42,
"nRecvMode":42,
"nSendMode":42
                   
                        }]
}
//获取拓扑图接口
function getTopological(){
	tptEvent(deviceTop)
	/*$._ajax({
		type: "POST",
		url : topologip+"/sie/httpjson/get_mesh_device_top",
		data :{},
		success : function(ret) {
			if(ret.nResultCode != 0){
				topologicalErrFun(ret);
				return;
			}
			if(ret.nResultCode == 0){
				$(".topologicalErr").css("display","none");
				$.hy_log("我获取到的值"+JSON.stringify(ret));	
				tptEvent(ret)
				
				
				
			}
		},
		fail:function(ret){
		}
	})*/
}
getTopological();

function tptEvent(ret){
	var viscsdata = ret.lstMeshDeviceTop;
	var j = 0;
	var data = {
		"nodes":[],
		"edges":[]
	}
	var dis = [[{
		"x":0,
		"y":0
		}],
		
		[
		{
		"x":-150,
		"y":-100
		},
		
		{
		"x":100,
		"y":60
		}
		],
		
		
		[{
		"x":-70,
		"y":80
		},
		{
		"x":100,
		"y":0
		},
		{
		"x":-80,
		"y":-100
		}
		],
		
		[{
		"x":-150,
		"y":-120
		},
		{
		"x":150,
		"y":-110
		},
		{
		"x":160,
		"y":120
		},
		{
		"x":-160,
		"y":130
		}
		]
		
		]
	
	var nodeslistindex = [];
	
	$.each(viscsdata,function(i,n){
		var thisip = n.srcdev.strIP;
		var thisip1 = n.desdev.strIP;
		if(nodeslistindex.indexOf(thisip) < 0){
			nodeslistindex.push(n.srcdev.strIP);
		}
		if(nodeslistindex.indexOf(thisip1) < 0){
			nodeslistindex.push(n.desdev.strIP);
		}
	});
	
	var nodelist = [];
	
	
	
	var mapdatarelationlist = [];   //地图上两两连线的关系点
	
	//$.hy_log("nodeslistindex"+JSON2.stringify(nodeslistindex))
	
	$.each(viscsdata,function(i,n){
		var thisip = n.srcdev.strIP;
		var nextip = n.desdev.strIP;
		function randomNum(minNum,maxNum){ 
		    switch(arguments.length){ 
		        case 1: 
		            return parseInt(Math.random()*minNum+1,10); 
		        break; 
		        case 2: 
		            return parseInt(Math.random()*(maxNum-minNum+1)+minNum,10); 
		        break; 
		            default: 
		                return 0; 
		            break; 
		    } 
		}
		if(nodelist.indexOf(thisip) < 0){
			nodelist.push(n.srcdev.strIP);
			if(dis[nodeslistindex.length-1]){
				var node = {
						"id":i,
						"label":n.srcdev.strIP,
						"x":dis[nodeslistindex.length-1][j].x||randomNum(-150,150),
						"y":dis[nodeslistindex.length-1][j].y||randomNum(-150,150)
					}
					
				
			}else{
				var node = {
					"id":indfrom,
					"label":n.srcdev.strIP,
					
				}
		    }
			++j;
			data.nodes.push(node);
		}
		if(nodelist.indexOf(nextip) < 0){
			nodelist.push(n.desdev.strIP);
			if(dis[nodeslistindex.length-1]){
				var node = {
						"id":i,
						"label":n.desdev.strIP,
						"x":dis[nodeslistindex.length-1][j].x,
						"y":dis[nodeslistindex.length-1][j].y
					}
					
				
			}else{
				var node = {
					"id":indfrom,
					"label":n.desdev.strIP,
					
				}
		    }
			++j;
			data.nodes.push(node);
		}
		
		
		
		
		console.log("拼接出来的设备列表"+JSON.stringify(data.nodes));
		
	})
	myGraph(data.nodes,viscsdata,dis);	
		    
}
function myGraph(mapdatarelationlist,viscsdata,dis){
	if(!window.getI18NString){getI18NString = function(s){return s;}}
	if($("#topologicalDiv1").css('display')=='none'){
		return;
	}
	$("#topologicalDiv2").remove();
	$("#topologicalDiv1").append($("<div id='topologicalDiv2' style='height: 380px;width:100%' class='topologicalDiv2'/>"));
	var graph = new Q.Graph(topologicalDiv2);

	graph.onclick = function(evt){
		var data = evt.getData();
		if(data instanceof Q.Group){
			var target = graph.hitTest(evt);
			if(target && target.type == "GroupHandle"){
				target.reverseExpanded();
			}
		}
	}
    
    $.each(mapdatarelationlist,function(i,ele){
		var name="node"+i;
	    window[name] = graph.createNode(ele.label, ele.x, ele.y);
    })
     $.each(viscsdata,function(j,ele){
     	var nSnr = ele.nSnr+"(db)";
     	var sIp = ele.srcdev.strIP
		var eIp = ele.desdev.strIP
		var s;
		var e;
     	for(var i=0; i<mapdatarelationlist.length; i++) {
     		if(mapdatarelationlist[i].label==sIp)
     			s = i;
     		if(mapdatarelationlist[i].label==eIp)
     			e = i;
		}
	
		var name1="node"+s;
		var name2="node"+e;
	    createEdge(nSnr,window[name1],window[name2]);
    })
	

	function createEdge(nSnr,f, t){
		return graph.createEdge(nSnr, f, t);
	}
	$($(".Q-Canvas")[1]).hide();
}
