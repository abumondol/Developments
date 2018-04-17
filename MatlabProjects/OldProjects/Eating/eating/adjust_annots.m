function res = adjust_annots(d, annots)
    count = size(annots, 1);
    res = zeros(count, 2);
    
    for i = 1:count
        t = annots(i, 1);        
        x = find(d(:,1)>=t);
        a = x(1);        
        label = annots(i, 2);
        
        if d(a, 1) > t + 1
            res(i,:) = [t, label];
            fprintf('************** Gravity data not available for annot : %d ********************', i );
            continue
        end
        
        if d(a, 2) >= d(a+1,2)
            while d(a, 2) > d(a+1,2)
                a = a+1;
            end
        else
            while d(a, 2) > d(a-1,2)
                a = a-1;
            end
        end
        res(i,:) = [d(a, 1), label];
        
    end
    
end