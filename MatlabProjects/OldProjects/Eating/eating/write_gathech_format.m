half_window_size = 2;
for i=16:36
    file_name = strcat('usc_gatech/', data(i).subject_id,'.csv')
    acl = data(i).accel;
    a = data(i).annots;
    t = acl(:,1);
    
    labels = zeros(length(acl), 1);
    annot_count = length(a)
    for j=1:annot_count
        if a(j,2)<400
            labels(t>=a(j,1)-half_window_size & t<=a(j,1)+half_window_size, 1) = 1;
        elseif a(j,2)>=400 && a(j,2)<1000
            labels(t>=a(j,1)-half_window_size & t<=a(j,1)+half_window_size, 1) = 2;    
        end        
    end
    acl = [acl, labels];
    csvwrite(file_name, acl);
end