half_window_size = 2;
for i=1:1
    file_name = strcat('formatted_data/', data(i).subject_id,'.csv')
    acl = data(i).accel;
    a = data(i).annots;
    t = acl(:,1);
    
    labels = zeros(length(acl), 1);
    annot_count = length(a)
    fprintf('Annot Count: %d\n', annot_count);
    for j=1:annot_count
        %fprintf('Labeling\n');
        if a(j,2)<400
            labels(t>=a(j,1)-half_window_size & t<=a(j,1)+half_window_size, 1) = 1;
        elseif a(j,2)>=400 && a(j,2)<1000
            labels(t>=a(j,1)-half_window_size & t<=a(j,1)+half_window_size, 1) = 2;    
        end        
    end
    acl = [acl, labels];
    csvwrite(file_name, acl);
end