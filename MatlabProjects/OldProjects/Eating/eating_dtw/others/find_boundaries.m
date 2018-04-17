sub_count = length(data)

for s = 1:sub_count
    fprintf('Subject %d\n', s); 
    acn = data(s).accel_norm(:,2:4);
    mp = segments(s).min_points;
    mp = mp(mp(:,3)>0, 1);
    mp_count = length(mp);
    res = zeros(mp_count, 4);
    for i=1:length(mp)
        p = mp(i);
        center = acn(p, :);
        
        j=p-16;
        last_proximity = sum(center.*acn(j,:), 2);
        while j>=p-64
            j = j-1;
            proximity = sum(center.*acn(j,:), 2);
            if proximity >= last_proximity || acn(j,1)>=0               
                break;                
            end            
            last_proximiy = proximity;
        end
        
        res(i, 1) = p-j;
        res(i, 3) = last_proximity;                        
        
        j=p+16;
        last_proximity = sum(center.*acn(j,:), 2);
        while j<=p+64
            j = j+1;
            proximity = sum(center.*acn(j,:), 2);
            if proximity >= last_proximity || acn(j,1)>=0             
                break;                
            end            
            last_proximiy = proximity;
        end
        
        res(i, 2) = j-p;
        res(i, 4) = last_proximity;
    end
    segments(s).boundaries = [mp, res];
end

save('segments', 'segments');