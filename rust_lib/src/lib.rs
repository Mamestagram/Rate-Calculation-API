use jni::JNIEnv;
use jni::objects::{JClass, JString, JObject};
use jni::sys::jobject;
use minacalc_rs::{Calc, RoxCalcExt};
use std::path::PathBuf;
use std::cell::RefCell;

thread_local! {
    static CALC: RefCell<Result<Calc, ()>> = RefCell::new(Calc::new().map_err(|_| ()));
}

#[no_mangle]
pub extern "system" fn Java_net_mamesosu_api_calculate_CalculateRate_processData<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    input: JString<'local>,
) -> jobject {
    let null_obj = JObject::null().into_raw();

    let input_str: String = env.get_string(&input).expect("文字列の取得に失敗しました").into();
    let path = PathBuf::from(&input_str);

    let calc_result = CALC.with(|calc_ref| {
        let mut calc_borrow = calc_ref.borrow_mut();
        if let Ok(calc) = &mut *calc_borrow {
            // エラー型を () に変換して、elseブロックの Err(()) と型を合わせる
            calc.calculate_all_rates_from_file(&path, true).map_err(|_| ())
        } else {
            Err(())
        }
    });

    let msd_results = match calc_result {
        Ok(res) => res,
        Err(_) => return null_obj,
    };

    let rate_indices = [0, 3, 8, 13];
    let mut flat_scores = [0.0f64; 32];

    for (i, &index) in rate_indices.iter().enumerate() {
        if index < msd_results.msds.len() {
            let scores = msd_results.msds[index];
            let offset = i * 8;
            flat_scores[offset]     = scores.overall as f64;
            flat_scores[offset + 1] = scores.stream as f64;
            flat_scores[offset + 2] = scores.jumpstream as f64;
            flat_scores[offset + 3] = scores.handstream as f64;
            flat_scores[offset + 4] = scores.stamina as f64;
            flat_scores[offset + 5] = scores.jackspeed as f64;
            flat_scores[offset + 6] = scores.chordjack as f64;
            flat_scores[offset + 7] = scores.technical as f64;
        }
    }

    let score_array = env.new_double_array(32).unwrap();
    env.set_double_array_region(&score_array, 0, &flat_scores).unwrap();

    score_array.into_raw()
}