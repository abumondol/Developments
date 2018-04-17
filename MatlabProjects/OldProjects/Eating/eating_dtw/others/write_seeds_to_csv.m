pos_seeds = [];
neg_seeds = [];
for s = 1:4:34
    accel = data(s).accel_norm;
    mp = segments(s).min_points;
    mp_count = length(mp);
    
    for i=1:mp_count
        if mp(i, 3)<0
            continue;
        end
        
        p = mp(i,1);
        a = accel(p-40:p+40, 2:4);
        a = a';
        a = a(:);
        a = a';
        
        if mp(i, 3)==0
            neg_seeds = [neg_seeds; a];
        else
            pos_seeds = [pos_seeds; a];
        end
    end    
end

csvwrite('pos_seeds.csv', pos_seeds);
csvwrite('neg_seeds.csv', neg_seeds);
