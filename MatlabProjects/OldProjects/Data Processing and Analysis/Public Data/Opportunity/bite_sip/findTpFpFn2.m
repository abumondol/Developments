function  [TP, FP, FN] = findTpFpFn2(gtix, resix, radius)    
    TP = 0; 
    FP = 0;
    FN = 0;
    if isempty(gtix)                
        FP = length(resix);        
        return;
    end
    
    if isempty(resix)        
        FN = length(gtix);        
        return;
    end
    
    gtcount = size(gtix, 1);
    rescount = size(resix, 1);
    
    for i = 1:gtcount 
        dist = abs(resix(:,1) - gtix(i, 1));
        a = find(dist<radius, 1);
        
        if isempty(a)            
            FN = FN + 1;            
        else            
            TP = TP + 1;
        end
         
    end
    
    for i = 1:rescount        
        dist = abs(gtix(:,1) - resix(i, 1));        
        a = find(dist<radius, 1);
        
        if isempty(a)            
            FP = FP + 1;                    
        end
         
    end
    
end