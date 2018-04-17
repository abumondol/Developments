function [bop, bow, bowbin] = bow_calculate_features(d, window_size, slide)
    sample_count = length(d);        
    f_count = floor((sample_count - window_size)/slide);    
    
    bop = zeros(f_count, 162);
    bow = zeros(f_count, 162);
    bowbin = zeros(f_count, 162);
    for i = 1:f_count
        s = (i-1)*slide+1;
        e = s + window_size-1;
        [bp, bw, bwb] = calculate_features_one_window(d(s:e, :));        
        bop(i, :) = bp;
        bow(i, :) = bw;
        bowbin(i, :) = bwb;
    end
end

function [bp, bw, bwb] = calculate_features_one_window(d)
    len = length(d);
    bp = zeros(1, 162);
    bw = zeros(1, 162);
    bwb = zeros(1, 162);
    
    res = [];    
    st = 1;
    for i = 2:len
        if d(i) == d(i-1)
        	continue
        else
            res = [res; d(i-1), i-st];
            st = i;
        end        
    end
    res = [res; d(i), i-st+1];
    
    len = length(res(:,1));
    for i= 1:len
        ix = res(i,1);        
        bp(ix) = bp(ix) + res(i,2);
        bw(ix) = bw(ix) + 1;
        bwb(ix) = 1;
    end    
end
        


