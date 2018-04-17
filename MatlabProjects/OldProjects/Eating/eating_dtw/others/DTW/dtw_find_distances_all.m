data_count = length(data);
distances = [];
for s = 1:36
    res_pos = [];
    res_neg = [];
    mp = segments(s).min_points;
    labels = segments(s).labels;
    x = data(s).accel(:, 2);
    acn = data(s).accel_norm;
    mp_count = length(mp);
    
    for i = 1:mp_count
        
        ix = mp(i);
        minx = x(ix);
        if labels(i,1)<=0 || labels(i,1)>=400 || labels(i,3)>32 || ix<80
            continue
        end
        
        
        ldata = acn(ix-79:ix, :);
        ldata = flipud(ldata);
        rdata = acn(ix:ix+79, :);
        
        for s2 = 1:data_count
            fprintf('Subjects: %d, %d, %d/%d\n', s, s2, i, mp_count);
            mp2 = segments(s2).min_points;
            labels2 = segments(s2).labels;
            x2 = data(s2).accel(:, 2);
            acn2 = data(s2).accel_norm;
            accel_count2 = length(x2);
            mp_count2 = length(mp2);
            
            for j=1:mp_count2                
                ix2 = mp2(j);
                minx2 = x2(ix2);
                if (labels2(j,1)>=400 && labels2(j,1)<1000) || labels2(j,3)>32 || abs( minx - minx2)>2 || ix2<80 ||ix2+79>accel_count2
                    continue
                end
                
                ldata2 = acn2(ix2-79:ix2, :);
                ldata2 = flipud(ldata2);
                rdata2 = acn2(ix2:ix2+79, :);
                
                ldist = DTW(ldata, ldata2);
                rdist = DTW(rdata, rdata2);
                
                if labels2(j,1)>0 & labels2(j,1)<400
                    res_pos = [res_pos; s2, j, labels2(j,1), minx2, ldist, rdist]; 
                else
                    res_neg = [res_neg; s2, j, labels2(j,1), minx2, ldist, rdist]; 
                end
                
            end

        end
        
        distances(s).index(i).minx = minx;
        distances(s).index(i).pos_dist = res_pos;
        distances(s).index(i).neg_dist = res_neg;
    end
end
