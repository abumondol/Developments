function res = check_bite_pattern(sid, index, distances, pats, type)
    res = 0;
    pat_count = length(pats);    
    for i = 1:pat_count
        pat_sid = pats(i, 1);
        pat_index = pats(i, 2);
        pat_radius = pats(i, 4);
        dist_index = 16;        

        if type == 1 
            d = distances(pat_sid).pos_pos;
        elseif type == 2
            d = distances(pat_sid).pos_neg;
        else
            d = distances(pat_sid).pos_neg2;
        end
        
        ix = d(:, 2) == pat_index & d(:, 6) == sid & d(:, 7)==index;            
        ix = find(ix);

        if length(ix)~=1
            fprintf('Length error in check_bite_pattern: %d', length(ix));
            exit(0);
            
        end
        
        r =  d(ix, dist_index);
        if r <= pat_radius
            res = i;                
            return;
        end
        
    end
    
end