function [TP, TN, FP, FN] = detect_subject_bites(sid, distances, pats, segments_for_pat)
    fprintf('Bite detection subject %d: ', sid);
    
    TP = 0;
    TN = 0;
    FP = 0;
    FN = 0;        
    
    count = length(segments_for_pat(sid).segspos);
    for i=1:count        
        res = check_bite_pattern(sid, i, distances, pats, 1);
        if res >= 1 
            TP = TP + 1;
        else
            FN = FN + 1;
        end        
    end
    
    count = length(segments_for_pat(sid).segsneg);
    for i=1:count
        if sid<=36
            res = check_bite_pattern(sid, i, distances, pats, 2);
        else
            res = check_bite_pattern(sid, i, distances, pats, 3);
        end
        if res >= 1 
            FP = FP + 1;
        else
            TN = TN + 1;
        end        
    end
    
    fprintf('TP: %d, TN: %d, FP:%d, FN:%d\n', TP, TN, FP, FN);
    
end
    
