segments = [];
sub_count = length(data)
th = -4;

for sub = 1:sub_count    
    accel = data(sub).accel;
    annots = data(sub).annots;            
    x = accel(:, 2);
    accel_count = length(accel);
    i = 1;
    while x(i) <= th
        i = i+1;
    end
    
    segments(sub).start = i;
    segments (sub).original_accel_count = accel_count;
    
    while x(accel_count) <= th
        accel_count = accel_count - 1;
    end
    
    segments (sub).new_accel_count = accel_count;    
    
    seg = [];    
    while i < accel_count
        if x(i)>= -4 && x(i+1)< -4
            s = i;
        elseif x(i)<= -4 && x(i+1)> -4            
            e = i;
            t1 = accel(s, 1);
            t2 = accel(e, 1);
            ac_e = 0;
            ac_d = 0;
            if ~isempty(annots)
                a = annots(annots(:, 1)>=t1 & annots(:, 1)<=t2 & annots(:, 2)<400, 1);            
                ac_e = length(a);
                
                a = annots(annots(:, 1)>=t1 & annots(:, 1)<=t2 & annots(:, 2)>=400 & annots(:, 2)<1000, 1);            
                ac_d= length(a);
            end            
            seg = [seg; s, e, t1, t2, t2-t1, ac_e, ac_d];
        end
        i = i +1;
    end
    
    if ~isempty(annots)
        segments(sub).segs = seg;
        a = annots(annots(:, 2)<400, 1);            
        segments(sub).total_eat_annots = length(a);
        a = annots(annots(:, 2)>=400 & annots(:, 2)<1000, 1);            
        segments(sub).total_drink_annots = length(a);     
    end
    
    segments(sub).eat_annots_covered = sum(seg(:,end-1));
    segments(sub).drink_annots_covered = sum(seg(:,end));
    
end

save('segments','segments');
