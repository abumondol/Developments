function [window_length, annot_type, free_length, step_size, x_th, var_th] = get_settings()
    window_length = 5*16;
    annot_type = 1;
    free_length = 2*16;
    step_size = 16;
    x_th = 0;
    var_th = .1;
end