function features_combined = features_combine(features, features_noneat, labels)        
    features_combined = [];
    sub_count = length(features);    
    for sub =1:sub_count
        sess_count = length(features(sub).session);
        for sess = 1:sess_count            
            f = features(sub).session(sess).features;
            l = labels(sub).session(sess).labels;
            features_combined = [features_combined; f, l];            
        end        
        
        sess_count = length(features_noneat(sub).session);
        for sess = 1:sess_count            
            f = features_noneat(sub).session(sess).features;
            l = zeros(size(f, 1), 1);
            features_combined = [features_combined; f, l];            
        end                
    end    
    
end

