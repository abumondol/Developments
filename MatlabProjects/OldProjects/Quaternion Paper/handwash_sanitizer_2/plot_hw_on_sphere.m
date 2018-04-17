
for s = 3:3
    ai = data(3).annot_indices;
    d = data(s).hw_data;
    segs = cell(5,1);
    for i = 1:5
        segs{i} = d(ai(i,1):ai(i,2), 2:4);
    end
end

spmo_plot_on_sphere(segs, [], ico, 3, '');

segs = cell(1,1);
segs{1} = data(3).eat_data(:, 2:4);
spmo_plot_on_sphere(segs, [], ico, 3, '');