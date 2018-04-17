function res = process_data(data, offset, annots, annot_corrections)    
    annots = sortrows(annots);
    if data(1,2) == 0
        data = data(2:end, :);
    end

    data(:,1) = data(:,1)-data(1,1);
    data(:,1) = data(:,1)/1e9 + offset;

    accel = data(data(:,2)==1, [1, 4:6]);
    %gyro = data(data(:,2)==4, [1, 4:6]);
    grav = data(data(:,2)==9, [1, 4:6]);
    %quat = data(data(:,2)==11, [1, 4:6]);

    %s = find(accel(:,1) == grav(1,1));
    %linaccel = [grav(:,1), accel(s:end, 2:4) - grav(:, 2:4)];
    
    accel = accel(1:4:end, :);
    grav = grav(1:4:end, :);    
    %accel(:,2:4) = mysmooth(accel(:,2:4), 0.8);
    %grav(:,2:4) = mysmooth(grav(:,2:4), 0.8);
    
    if isempty(annots)
        res.accel = accel;                
        res.grav = grav;
        res.annots = [];
        res.annots_adjusted = [];
        res.eat_annot_count = 0;
        res.drink_annot_count = 0;
        res.non_bite_annot_count = 0;    
        res.annot_corrections = annot_corrections;
        return
    end
    
    %annots_adjusted = adjust_annots(grav, annots);
    %annots = correct_annots(annots, annot_corrections);

    accel_annot_indices = get_annot_indices(accel, annots);
    %gyro_annot_indices = get_annot_indices(gyro, annots);
    %grav_annot_indices = get_annot_indices(grav, annots);
    %quat_annot_indices = get_annot_indices(quat, annots);

    res.accel = accel;                
    res.grav = grav;
    %res.gyro = gyro;            
    %res.linaccel = linaccel;
    %res.quat = quat;
    %res.accel_annot_indices = accel_annot_indices;
    %res.gyro_annot_indices = gyro_annot_indices;
    %res.grav_annot_indices = grav_annot_indices;
    %res.quat_annot_indices = quat_annot_indices;

    res.annots = [annots, accel_annot_indices(:, 1)];    
    %res.annots_adjusted = sortrows(annots_adjusted);
    res.eat_annot_count = sum(annots(:,2)<400);
    res.drink_annot_count = sum(annots(:,2)>=400 & annots(:,2)<1000);
    res.non_bite_annot_count = sum(annots(:,2)>=1000);    
    %res.annot_corrections = annot_corrections;
end    


