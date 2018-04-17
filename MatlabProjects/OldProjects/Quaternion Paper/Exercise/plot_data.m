for i=1:8
    t = annots(annots(:,1)==i, :);
    s= t(:, 3)*60 + t(:, 4);
    e = t(:, 5)*60 + t(:, 6);
    
    d = cell(3,1);
    d{1}= data(data(:,1)>=s(1)& data(:,1)<=e(1), 2:4);
    d{2}= data(data(:,1)>=s(2)& data(:,1)<=e(2), 2:4);
    d{3}= data(data(:,1)>=s(3)& data(:,1)<=e(3), 2:4);
    
    spmo_plot_on_sphere_ex(d, [], ico, 2, '')
end