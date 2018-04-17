function pats = find_patterns_for_subject(sid, distances)
    pats = [];
    sr = get_subject_data_range(sid);      
    %fprintf('Subject: %d\n', sid);
    
    res = [];
    k = 1;
    for s = 1:length(distances)
        if s >= sr(1) && s <= sr(2)
            continue
        end
        
        d_pos = distances(s).pos_pos;
        d_neg = distances(s).pos_neg; 
        d_neg2 = distances(s).pos_neg2; 
        
        d_pos = d_pos(d_pos(:, 6) < sr(1) | d_pos(:, 6) > sr(2), :);        
        d_neg = d_neg(d_neg(:, 6) < sr(1) | d_neg(:, 6) > sr(2), :);        
        d_neg2 = d_neg2(d_neg2(:, 6) < sr(1) | d_neg2(:, 6) > sr(2), :);
        %d_neg=[d_neg; d_neg2];
        
        %length(d_neg)
        indices = d_pos(:, 2);
        indices = sort(unique(indices));
        count = length(indices);
        %fprintf('Subject: %d, Pos indices count: %d\n', s, count);
        col = 16;
        
        for i=1:count
            if indices(i) ~= i
                fprintf('indices(i) is not equal to i');
                exit(0);
            end
                dn = d_neg(d_neg(:, 2) == i, :);
                dn = sort(dn(:, 15));
                mn = dn(1);
            
                dp = d_pos( d_pos(:, 2) == i & d_pos(:, col)<=mn, :);                
                if isempty(dp)
                    avg = mn/2;
                else
                    mx = max(dp(:, col));
                    avg = (mx+mn)/2;
                end

                res(k).subject = s;
                res(k).index = indices(i);
                res(k).covered_count = size(dp, 1);
                res(k).radius = avg;
                res(k).covered = dp(:, 6:7);
                k = k+1;
        end
    end
    
    save('res', 'res');

    fprintf('Finished finding covered and distance: %d\n', sid);
    
    %fprintf('Alive size: ');
    count = length(res);        
    alive = zeros(count, 1)+1;    
    while sum(alive)>0  
        %fprintf('\n%d', sum(alive));
        max_covrered_count = -1;
        max_radius = -1;
        max_ix = 0;
        for i=1:count
            if alive(i) >0 && ( res(i).covered_count > max_covrered_count || ( res(i).covered_count==max_covrered_count && res(i).radius > max_radius) )
                max_covrered_count =  res(i).covered_count;
                max_radius = res(i).radius;
                max_ix = i;
            end
        end
        
        alive(max_ix) = 0; 
        cv = res(max_ix).covered;      
        len = size(cv, 1);
        for i = 1:len
            ix = search_subject_index(res, cv(i, 1), cv(i,2));
            %fprintf('%d, %d, %d, %d\n', i, cv(i, 1), cv(i, 2), ix);
            alive(ix) = 0;
        end

        pats = [pats; res(max_ix).subject, res(max_ix).index, res(max_ix).covered_count, res(max_ix).radius]; 
    end
    
end
    
